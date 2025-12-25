package com.nicecommerce.accounts.service;

import com.google.firebase.auth.UserRecord;
import com.nicecommerce.accounts.dto.*;
import com.nicecommerce.accounts.entity.User;
import com.nicecommerce.accounts.repository.UserRepository;
import com.nicecommerce.core.exception.BusinessException;
import com.nicecommerce.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * User Service
 * 
 * Business logic for user management and authentication.
 * 
 * @author NiceCommerce Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Register a new user using Firebase
     * 
     * Creates user in both Firebase and local database.
     * 
     * @param request Sign up request with user details
     * @return User DTO with Firebase UID
     */
    @Transactional
    public UserDTO signUp(SignUpRequest request) {
        // Check if user already exists in local database
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        // Create user in Firebase
        UserRecord firebaseUser = firebaseAuthService.createUser(request);

        // Create user in local database
        User user = User.builder()
                .firebaseUid(firebaseUser.getUid())
                .email(firebaseUser.getEmail())
                .displayName(firebaseUser.getDisplayName() != null ? 
                        firebaseUser.getDisplayName() : request.getDisplayName())
                .phoneNumber(firebaseUser.getPhoneNumber() != null ? 
                        firebaseUser.getPhoneNumber() : request.getPhoneNumber())
                .emailVerified(firebaseUser.isEmailVerified())
                .role(User.UserRole.CUSTOMER)
                .isActive(!firebaseUser.isDisabled())
                .build();

        user = userRepository.save(user);
        log.info("User registered in Firebase and local DB: {}", user.getEmail());

        return mapToDTO(user, firebaseUser.getUid());
    }

    /**
     * Sign in user using Firebase ID token
     * 
     * @param request Sign in request with Firebase ID token
     * @return Sign in response with user information
     */
    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest request) {
        // Verify token and get user info from Firebase
        SignInResponse firebaseResponse = firebaseAuthService.signIn(request.getIdToken());
        
        // Update local database user if exists, or create if doesn't
        User localUser = userRepository.findByEmail(firebaseResponse.getEmail())
                .orElseGet(() -> {
                    // Create local user if doesn't exist (for users created directly in Firebase)
                    User newUser = User.builder()
                            .firebaseUid(firebaseResponse.getUid())
                            .email(firebaseResponse.getEmail())
                            .displayName(firebaseResponse.getDisplayName())
                            .phoneNumber(firebaseResponse.getPhoneNumber())
                            .emailVerified(firebaseResponse.getEmailVerified())
                            .role(User.UserRole.CUSTOMER)
                            .isActive(true)
                            .build();
                    return userRepository.save(newUser);
                });

        // Update last login
        localUser.setLastLoginAt(LocalDateTime.now());
        userRepository.save(localUser);

        return firebaseResponse;
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDTO(user);
    }

    /**
     * Get current user
     */
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDTO(user, null);
    }

    /**
     * Update user in Firebase and local database
     * 
     * @param uid Firebase user ID
     * @param request Update request with new user details
     * @return Updated user DTO
     */
    @Transactional
    public UpdateUserResponse updateUser(String uid, UpdateUserRequest request) {
        // Update in Firebase
        UserRecord firebaseUser = firebaseAuthService.updateUser(uid, request);

        // Update in local database
        User localUser = userRepository.findByEmail(firebaseUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getDisplayName() != null) {
            localUser.setDisplayName(request.getDisplayName());
        }
        if (request.getPhoneNumber() != null) {
            localUser.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmailVerified() != null) {
            localUser.setEmailVerified(request.getEmailVerified());
        }
        if (request.getRole() != null) {
            localUser.setRole(request.getRole());
        }

        localUser = userRepository.save(localUser);
        log.info("User updated in Firebase and local DB: {}", localUser.getEmail());

        return UpdateUserResponse.builder()
                .uid(firebaseUser.getUid())
                .email(firebaseUser.getEmail())
                .displayName(firebaseUser.getDisplayName())
                .phoneNumber(firebaseUser.getPhoneNumber())
                .emailVerified(firebaseUser.isEmailVerified())
                .build();
    }

    /**
     * Sign out user (revoke Firebase tokens)
     * 
     * @param uid Firebase user ID
     */
    public void signOut(String uid) {
        firebaseAuthService.signOut(uid);
        log.info("User signed out: {}", uid);
    }

    /**
     * Send password reset email
     * 
     * @param request Password reset request with email
     */
    public void recoverPassword(PasswordResetRequest request) {
        firebaseAuthService.sendPasswordResetEmail(request.getEmail());
        log.info("Password reset email sent to: {}", request.getEmail());
    }

    /**
     * Map User entity to UserDTO
     */
    private UserDTO mapToDTO(User user, String firebaseUid) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .phoneNumber(user.getPhoneNumber())
                .emailVerified(user.getEmailVerified())
                .role(user.getRole())
                .staffRole(user.getStaffRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

