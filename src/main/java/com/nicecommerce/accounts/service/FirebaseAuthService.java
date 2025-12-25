package com.nicecommerce.accounts.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.nicecommerce.accounts.dto.*;
import com.nicecommerce.core.exception.BusinessException;
import com.nicecommerce.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Firebase Authentication Service
 * 
 * Handles all Firebase authentication operations:
 * - Create user
 * - Update user
 * - Sign in (verify token)
 * - Sign out (revoke tokens)
 * - Password recovery
 * 
 * @author NiceCommerce Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseAuthService {

    private final FirebaseAuth firebaseAuth;

    /**
     * Create a new user in Firebase
     * 
     * @param request Sign up request with user details
     * @return Firebase user record
     * @throws BusinessException if user creation fails
     */
    public UserRecord createUser(SignUpRequest request) {
        try {
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setEmailVerified(false)
                    .setDisabled(false);

            // Set display name if provided
            if (request.getDisplayName() != null && !request.getDisplayName().isEmpty()) {
                createRequest.setDisplayName(request.getDisplayName());
            }

            // Set phone number if provided
            if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
                createRequest.setPhoneNumber(request.getPhoneNumber());
            }

            // Set custom claims (roles, etc.)
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "CUSTOMER");
            createRequest.setCustomClaims(claims);

            UserRecord userRecord = firebaseAuth.createUser(createRequest);
            log.info("Firebase user created: {}", userRecord.getUid());
            return userRecord;
        } catch (FirebaseAuthException e) {
            log.error("Error creating Firebase user: {}", e.getMessage(), e);
            throw new BusinessException("Failed to create user: " + e.getMessage());
        }
    }

    /**
     * Update user in Firebase
     * 
     * @param uid Firebase user ID
     * @param request Update request with new user details
     * @return Updated Firebase user record
     * @throws ResourceNotFoundException if user not found
     * @throws BusinessException if update fails
     */
    public UserRecord updateUser(String uid, UpdateUserRequest request) {
        try {
            // Verify user exists
            UserRecord existingUser = firebaseAuth.getUser(uid);
            
            UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(uid);

            // Update email if provided
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                updateRequest.setEmail(request.getEmail());
            }

            // Update display name if provided
            if (request.getDisplayName() != null) {
                updateRequest.setDisplayName(request.getDisplayName());
            }

            // Update phone number if provided
            if (request.getPhoneNumber() != null) {
                updateRequest.setPhoneNumber(request.getPhoneNumber());
            }

            // Update password if provided
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                updateRequest.setPassword(request.getPassword());
            }

            // Update email verified status if provided
            if (request.getEmailVerified() != null) {
                updateRequest.setEmailVerified(request.getEmailVerified());
            }

            // Update custom claims if role is provided
            if (request.getRole() != null) {
                Map<String, Object> claims = new HashMap<>();
                if (existingUser.getCustomClaims() != null) {
                    claims.putAll(existingUser.getCustomClaims());
                }
                claims.put("role", request.getRole().name());
                updateRequest.setCustomClaims(claims);
            }

            UserRecord updatedUser = firebaseAuth.updateUser(updateRequest);
            log.info("Firebase user updated: {}", updatedUser.getUid());
            return updatedUser;
        } catch (FirebaseAuthException e) {
            if (e.getErrorCode().equals("user-not-found")) {
                throw new ResourceNotFoundException("User not found with uid: " + uid);
            }
            log.error("Error updating Firebase user: {}", e.getMessage(), e);
            throw new BusinessException("Failed to update user: " + e.getMessage());
        }
    }

    /**
     * Verify Firebase ID token and return user information
     * 
     * @param idToken Firebase ID token from client
     * @return Verified Firebase token with user claims
     * @throws BusinessException if token is invalid
     */
    public FirebaseToken verifyToken(String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            log.debug("Firebase token verified for user: {}", decodedToken.getUid());
            return decodedToken;
        } catch (FirebaseAuthException e) {
            log.error("Error verifying Firebase token: {}", e.getMessage(), e);
            throw new BusinessException("Invalid or expired token: " + e.getMessage());
        }
    }

    /**
     * Sign in user by verifying Firebase ID token
     * 
     * @param idToken Firebase ID token
     * @return Sign in response with user information
     */
    public SignInResponse signIn(String idToken) {
        FirebaseToken decodedToken = verifyToken(idToken);
        
        try {
            UserRecord userRecord = firebaseAuth.getUser(decodedToken.getUid());
            
            return SignInResponse.builder()
                    .uid(userRecord.getUid())
                    .email(userRecord.getEmail())
                    .displayName(userRecord.getDisplayName())
                    .emailVerified(userRecord.isEmailVerified())
                    .phoneNumber(userRecord.getPhoneNumber())
                    .photoUrl(userRecord.getPhotoUrl())
                    .customClaims(decodedToken.getClaims())
                    .build();
        } catch (FirebaseAuthException e) {
            log.error("Error getting user from Firebase: {}", e.getMessage(), e);
            throw new BusinessException("Failed to get user information: " + e.getMessage());
        }
    }

    /**
     * Sign out user by revoking all refresh tokens
     * This invalidates all sessions for the user
     * 
     * @param uid Firebase user ID
     * @throws ResourceNotFoundException if user not found
     * @throws BusinessException if revocation fails
     */
    public void signOut(String uid) {
        try {
            firebaseAuth.revokeRefreshTokens(uid);
            log.info("Firebase user signed out (tokens revoked): {}", uid);
        } catch (FirebaseAuthException e) {
            if (e.getErrorCode().equals("user-not-found")) {
                throw new ResourceNotFoundException("User not found with uid: " + uid);
            }
            log.error("Error signing out Firebase user: {}", e.getMessage(), e);
            throw new BusinessException("Failed to sign out user: " + e.getMessage());
        }
    }

    /**
     * Send password reset email
     * 
     * @param email User email address
     * @throws BusinessException if email sending fails
     */
    public void sendPasswordResetEmail(String email) {
        try {
            String link = firebaseAuth.generatePasswordResetLink(email);
            // In a real application, you would send this link via email
            // For now, we log it (in production, use email service)
            log.info("Password reset link generated for: {}", email);
            log.debug("Password reset link: {}", link);
            
            // TODO: Send email with reset link using email service
            // emailService.sendPasswordResetEmail(email, link);
        } catch (FirebaseAuthException e) {
            if (e.getErrorCode().equals("user-not-found")) {
                // Don't reveal if user exists for security
                log.warn("Password reset requested for non-existent user: {}", email);
                // Still return success to prevent email enumeration
                return;
            }
            log.error("Error sending password reset email: {}", e.getMessage(), e);
            throw new BusinessException("Failed to send password reset email: " + e.getMessage());
        }
    }

    /**
     * Get user by Firebase UID
     * 
     * @param uid Firebase user ID
     * @return Firebase user record
     * @throws ResourceNotFoundException if user not found
     */
    public UserRecord getUserByUid(String uid) {
        try {
            return firebaseAuth.getUser(uid);
        } catch (FirebaseAuthException e) {
            if (e.getErrorCode().equals("user-not-found")) {
                throw new ResourceNotFoundException("User not found with uid: " + uid);
            }
            log.error("Error getting Firebase user: {}", e.getMessage(), e);
            throw new BusinessException("Failed to get user: " + e.getMessage());
        }
    }

    /**
     * Get user by email
     * 
     * @param email User email address
     * @return Firebase user record
     * @throws ResourceNotFoundException if user not found
     */
    public UserRecord getUserByEmail(String email) {
        try {
            return firebaseAuth.getUserByEmail(email);
        } catch (FirebaseAuthException e) {
            if (e.getErrorCode().equals("user-not-found")) {
                throw new ResourceNotFoundException("User not found with email: " + email);
            }
            log.error("Error getting Firebase user by email: {}", e.getMessage(), e);
            throw new BusinessException("Failed to get user: " + e.getMessage());
        }
    }

    /**
     * Delete user from Firebase
     * 
     * @param uid Firebase user ID
     * @throws ResourceNotFoundException if user not found
     * @throws BusinessException if deletion fails
     */
    public void deleteUser(String uid) {
        try {
            firebaseAuth.deleteUser(uid);
            log.info("Firebase user deleted: {}", uid);
        } catch (FirebaseAuthException e) {
            if (e.getErrorCode().equals("user-not-found")) {
                throw new ResourceNotFoundException("User not found with uid: " + uid);
            }
            log.error("Error deleting Firebase user: {}", e.getMessage(), e);
            throw new BusinessException("Failed to delete user: " + e.getMessage());
        }
    }
}

