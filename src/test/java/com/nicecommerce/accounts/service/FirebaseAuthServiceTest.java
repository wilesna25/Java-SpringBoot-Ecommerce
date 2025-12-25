package com.nicecommerce.accounts.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.nicecommerce.accounts.dto.SignInResponse;
import com.nicecommerce.accounts.dto.SignUpRequest;
import com.nicecommerce.accounts.dto.UpdateUserRequest;
import com.nicecommerce.core.exception.BusinessException;
import com.nicecommerce.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for FirebaseAuthService
 * 
 * Tests all Firebase authentication operations with mocked Firebase SDK.
 * 
 * @author NiceCommerce Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FirebaseAuthService Unit Tests")
class FirebaseAuthServiceTest {

    @Mock
    private FirebaseAuth firebaseAuth;

    @InjectMocks
    private FirebaseAuthService firebaseAuthService;

    private UserRecord mockUserRecord;
    private FirebaseToken mockFirebaseToken;

    @BeforeEach
    void setUp() throws FirebaseAuthException {
        // Setup mock user record
        mockUserRecord = mock(UserRecord.class);
        when(mockUserRecord.getUid()).thenReturn("test-uid-123");
        when(mockUserRecord.getEmail()).thenReturn("test@example.com");
        when(mockUserRecord.getDisplayName()).thenReturn("Test User");
        when(mockUserRecord.getPhoneNumber()).thenReturn("+1234567890");
        when(mockUserRecord.isEmailVerified()).thenReturn(true);
        when(mockUserRecord.isDisabled()).thenReturn(false);
        when(mockUserRecord.getPhotoUrl()).thenReturn("https://example.com/photo.jpg");
        when(mockUserRecord.getCustomClaims()).thenReturn(new HashMap<>());

        // Setup mock Firebase token
        mockFirebaseToken = mock(FirebaseToken.class);
        when(mockFirebaseToken.getUid()).thenReturn("test-uid-123");
        when(mockFirebaseToken.getClaims()).thenReturn(new HashMap<>());
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser_Success() throws FirebaseAuthException {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("newuser@example.com")
                .password("password123")
                .displayName("New User")
                .phoneNumber("+1234567890")
                .build();

        when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class)))
                .thenReturn(mockUserRecord);

        // When
        UserRecord result = firebaseAuthService.createUser(request);

        // Then
        assertNotNull(result);
        assertEquals("test-uid-123", result.getUid());
        verify(firebaseAuth, times(1)).createUser(any(UserRecord.CreateRequest.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when user creation fails")
    void testCreateUser_Failure() throws FirebaseAuthException {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("existing@example.com")
                .password("password123")
                .build();

        when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class)))
                .thenThrow(new FirebaseAuthException("email-already-exists", "Email already exists"));

        // When & Then
        assertThrows(BusinessException.class, () -> firebaseAuthService.createUser(request));
        verify(firebaseAuth, times(1)).createUser(any(UserRecord.CreateRequest.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser_Success() throws FirebaseAuthException {
        // Given
        String uid = "test-uid-123";
        UpdateUserRequest request = UpdateUserRequest.builder()
                .displayName("Updated Name")
                .email("updated@example.com")
                .build();

        when(firebaseAuth.getUser(uid)).thenReturn(mockUserRecord);
        when(firebaseAuth.updateUser(any(UserRecord.UpdateRequest.class)))
                .thenReturn(mockUserRecord);

        // When
        UserRecord result = firebaseAuthService.updateUser(uid, request);

        // Then
        assertNotNull(result);
        verify(firebaseAuth, times(1)).getUser(uid);
        verify(firebaseAuth, times(1)).updateUser(any(UserRecord.UpdateRequest.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent user")
    void testUpdateUser_UserNotFound() throws FirebaseAuthException {
        // Given
        String uid = "non-existent-uid";
        UpdateUserRequest request = UpdateUserRequest.builder()
                .displayName("Updated Name")
                .build();

        when(firebaseAuth.getUser(uid))
                .thenThrow(new FirebaseAuthException("user-not-found", "User not found"));

        // When & Then
        assertThrows(ResourceNotFoundException.class, 
                () -> firebaseAuthService.updateUser(uid, request));
    }

    @Test
    @DisplayName("Should verify token successfully")
    void testVerifyToken_Success() throws FirebaseAuthException {
        // Given
        String idToken = "valid-firebase-token";

        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(mockFirebaseToken);

        // When
        FirebaseToken result = firebaseAuthService.verifyToken(idToken);

        // Then
        assertNotNull(result);
        assertEquals("test-uid-123", result.getUid());
        verify(firebaseAuth, times(1)).verifyIdToken(idToken);
    }

    @Test
    @DisplayName("Should throw BusinessException when token is invalid")
    void testVerifyToken_InvalidToken() throws FirebaseAuthException {
        // Given
        String idToken = "invalid-token";

        when(firebaseAuth.verifyIdToken(idToken))
                .thenThrow(new FirebaseAuthException("invalid-token", "Token is invalid"));

        // When & Then
        assertThrows(BusinessException.class, () -> firebaseAuthService.verifyToken(idToken));
    }

    @Test
    @DisplayName("Should sign in user successfully")
    void testSignIn_Success() throws FirebaseAuthException {
        // Given
        String idToken = "valid-firebase-token";

        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(mockFirebaseToken);
        when(firebaseAuth.getUser("test-uid-123")).thenReturn(mockUserRecord);

        // When
        SignInResponse result = firebaseAuthService.signIn(idToken);

        // Then
        assertNotNull(result);
        assertEquals("test-uid-123", result.getUid());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getDisplayName());
        assertTrue(result.getEmailVerified());
        verify(firebaseAuth, times(1)).verifyIdToken(idToken);
        verify(firebaseAuth, times(1)).getUser("test-uid-123");
    }

    @Test
    @DisplayName("Should sign out user successfully")
    void testSignOut_Success() throws FirebaseAuthException {
        // Given
        String uid = "test-uid-123";

        doNothing().when(firebaseAuth).revokeRefreshTokens(uid);

        // When
        assertDoesNotThrow(() -> firebaseAuthService.signOut(uid));

        // Then
        verify(firebaseAuth, times(1)).revokeRefreshTokens(uid);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when signing out non-existent user")
    void testSignOut_UserNotFound() throws FirebaseAuthException {
        // Given
        String uid = "non-existent-uid";

        doThrow(new FirebaseAuthException("user-not-found", "User not found"))
                .when(firebaseAuth).revokeRefreshTokens(uid);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> firebaseAuthService.signOut(uid));
    }

    @Test
    @DisplayName("Should send password reset email successfully")
    void testSendPasswordResetEmail_Success() throws FirebaseAuthException {
        // Given
        String email = "user@example.com";
        String resetLink = "https://example.com/reset?token=abc123";

        when(firebaseAuth.generatePasswordResetLink(email)).thenReturn(resetLink);

        // When
        assertDoesNotThrow(() -> firebaseAuthService.sendPasswordResetEmail(email));

        // Then
        verify(firebaseAuth, times(1)).generatePasswordResetLink(email);
    }

    @Test
    @DisplayName("Should handle password reset for non-existent user gracefully")
    void testSendPasswordResetEmail_UserNotFound() throws FirebaseAuthException {
        // Given
        String email = "nonexistent@example.com";

        when(firebaseAuth.generatePasswordResetLink(email))
                .thenThrow(new FirebaseAuthException("user-not-found", "User not found"));

        // When & Then - Should not throw exception (security: don't reveal if user exists)
        assertDoesNotThrow(() -> firebaseAuthService.sendPasswordResetEmail(email));
    }

    @Test
    @DisplayName("Should get user by UID successfully")
    void testGetUserByUid_Success() throws FirebaseAuthException {
        // Given
        String uid = "test-uid-123";

        when(firebaseAuth.getUser(uid)).thenReturn(mockUserRecord);

        // When
        UserRecord result = firebaseAuthService.getUserByUid(uid);

        // Then
        assertNotNull(result);
        assertEquals(uid, result.getUid());
        verify(firebaseAuth, times(1)).getUser(uid);
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void testGetUserByEmail_Success() throws FirebaseAuthException {
        // Given
        String email = "test@example.com";

        when(firebaseAuth.getUserByEmail(email)).thenReturn(mockUserRecord);

        // When
        UserRecord result = firebaseAuthService.getUserByEmail(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(firebaseAuth, times(1)).getUserByEmail(email);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_Success() throws FirebaseAuthException {
        // Given
        String uid = "test-uid-123";

        doNothing().when(firebaseAuth).deleteUser(uid);

        // When
        assertDoesNotThrow(() -> firebaseAuthService.deleteUser(uid));

        // Then
        verify(firebaseAuth, times(1)).deleteUser(uid);
    }
}

