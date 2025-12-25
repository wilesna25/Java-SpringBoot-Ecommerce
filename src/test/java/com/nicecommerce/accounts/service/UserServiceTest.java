package com.nicecommerce.accounts.service;

import com.google.firebase.auth.UserRecord;
import com.nicecommerce.accounts.dto.*;
import com.nicecommerce.accounts.entity.User;
import com.nicecommerce.accounts.repository.UserRepository;
import com.nicecommerce.core.exception.BusinessException;
import com.nicecommerce.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for UserService
 * 
 * Tests user service operations with mocked dependencies.
 * 
 * @author NiceCommerce Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private UserRecord mockFirebaseUser;

    @BeforeEach
    void setUp() {
        // Setup mock user
        mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .displayName("Test User")
                .phoneNumber("+1234567890")
                .emailVerified(true)
                .role(User.UserRole.CUSTOMER)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Setup mock Firebase user
        mockFirebaseUser = mock(UserRecord.class);
        when(mockFirebaseUser.getUid()).thenReturn("firebase-uid-123");
        when(mockFirebaseUser.getEmail()).thenReturn("test@example.com");
        when(mockFirebaseUser.getDisplayName()).thenReturn("Test User");
        when(mockFirebaseUser.getPhoneNumber()).thenReturn("+1234567890");
        when(mockFirebaseUser.isEmailVerified()).thenReturn(true);
        when(mockFirebaseUser.isDisabled()).thenReturn(false);
    }

    @Test
    @DisplayName("Should sign up user successfully")
    void testSignUp_Success() {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("newuser@example.com")
                .password("password123")
                .displayName("New User")
                .phoneNumber("+1234567890")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(firebaseAuthService.createUser(request)).thenReturn(mockFirebaseUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        UserDTO result = userService.signUp(request);

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(firebaseAuthService, times(1)).createUser(request);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when email already exists")
    void testSignUp_EmailExists() {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("existing@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(BusinessException.class, () -> userService.signUp(request));
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(firebaseAuthService, never()).createUser(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should sign in user successfully")
    void testSignIn_Success() {
        // Given
        String idToken = "valid-firebase-token";
        SignInRequest request = new SignInRequest(idToken);

        SignInResponse firebaseResponse = SignInResponse.builder()
                .uid("firebase-uid-123")
                .email("test@example.com")
                .displayName("Test User")
                .emailVerified(true)
                .build();

        when(firebaseAuthService.signIn(idToken)).thenReturn(firebaseResponse);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        SignInResponse result = userService.signIn(request);

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(firebaseAuthService, times(1)).signIn(idToken);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should create local user if doesn't exist during sign in")
    void testSignIn_CreateLocalUser() {
        // Given
        String idToken = "valid-firebase-token";
        SignInRequest request = new SignInRequest(idToken);

        SignInResponse firebaseResponse = SignInResponse.builder()
                .uid("firebase-uid-123")
                .email("newuser@example.com")
                .displayName("New User")
                .emailVerified(true)
                .build();

        when(firebaseAuthService.signIn(idToken)).thenReturn(firebaseResponse);
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        SignInResponse result = userService.signIn(request);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserById_Success() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // When
        UserDTO result = userService.getUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found by ID")
    void testGetUserById_NotFound() {
        // Given
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    @DisplayName("Should get current user successfully")
    void testGetCurrentUser_Success() {
        // Given
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // When
        UserDTO result = userService.getCurrentUser(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser_Success() {
        // Given
        String uid = "firebase-uid-123";
        UpdateUserRequest request = UpdateUserRequest.builder()
                .displayName("Updated Name")
                .phoneNumber("+9876543210")
                .build();

        UserRecord updatedFirebaseUser = mock(UserRecord.class);
        when(updatedFirebaseUser.getUid()).thenReturn(uid);
        when(updatedFirebaseUser.getEmail()).thenReturn("test@example.com");
        when(updatedFirebaseUser.getDisplayName()).thenReturn("Updated Name");
        when(updatedFirebaseUser.getPhoneNumber()).thenReturn("+9876543210");
        when(updatedFirebaseUser.isEmailVerified()).thenReturn(true);

        when(firebaseAuthService.updateUser(uid, request)).thenReturn(updatedFirebaseUser);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        UpdateUserResponse result = userService.updateUser(uid, request);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getDisplayName());
        verify(firebaseAuthService, times(1)).updateUser(uid, request);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should sign out user successfully")
    void testSignOut_Success() {
        // Given
        String uid = "firebase-uid-123";

        doNothing().when(firebaseAuthService).signOut(uid);

        // When
        assertDoesNotThrow(() -> userService.signOut(uid));

        // Then
        verify(firebaseAuthService, times(1)).signOut(uid);
    }

    @Test
    @DisplayName("Should recover password successfully")
    void testRecoverPassword_Success() {
        // Given
        PasswordResetRequest request = new PasswordResetRequest("user@example.com");

        doNothing().when(firebaseAuthService).sendPasswordResetEmail("user@example.com");

        // When
        assertDoesNotThrow(() -> userService.recoverPassword(request));

        // Then
        verify(firebaseAuthService, times(1)).sendPasswordResetEmail("user@example.com");
    }
}

