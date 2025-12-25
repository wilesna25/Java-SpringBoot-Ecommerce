package com.nicecommerce.accounts.integration;

import com.nicecommerce.accounts.dto.*;
import com.nicecommerce.accounts.entity.User;
import com.nicecommerce.accounts.repository.UserRepository;
import com.nicecommerce.accounts.service.FirebaseAuthService;
import com.nicecommerce.accounts.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Functional/Integration Tests for Complete Auth Flow
 * 
 * Tests the complete authentication flow from signup to signout.
 * Uses real database but mocks Firebase operations.
 * 
 * @author NiceCommerce Team
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Auth Flow Integration Tests")
class AuthFlowIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private FirebaseAuthService firebaseAuthService;

    private UserRecord mockFirebaseUser;
    private SignInResponse mockSignInResponse;

    @BeforeEach
    void setUp() {
        // Setup mock Firebase user
        mockFirebaseUser = mock(UserRecord.class);
        when(mockFirebaseUser.getUid()).thenReturn("firebase-uid-123");
        when(mockFirebaseUser.getEmail()).thenReturn("test@example.com");
        when(mockFirebaseUser.getDisplayName()).thenReturn("Test User");
        when(mockFirebaseUser.getPhoneNumber()).thenReturn("+1234567890");
        when(mockFirebaseUser.isEmailVerified()).thenReturn(false);
        when(mockFirebaseUser.isDisabled()).thenReturn(false);

        // Setup mock sign in response
        mockSignInResponse = SignInResponse.builder()
                .uid("firebase-uid-123")
                .email("test@example.com")
                .displayName("Test User")
                .emailVerified(true)
                .build();
    }

    @Test
    @DisplayName("Complete flow: Sign up -> Sign in -> Update -> Sign out")
    void testCompleteAuthFlow() {
        // Step 1: Sign Up
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("newuser@example.com")
                .password("password123")
                .displayName("New User")
                .phoneNumber("+1234567890")
                .build();

        when(firebaseAuthService.createUser(any(SignUpRequest.class))).thenReturn(mockFirebaseUser);

        UserDTO signedUpUser = userService.signUp(signUpRequest);
        assertNotNull(signedUpUser);
        assertEquals("test@example.com", signedUpUser.getEmail());

        // Verify user was saved in database
        Optional<User> savedUser = userRepository.findByEmail("test@example.com");
        assertTrue(savedUser.isPresent());
        assertEquals("test@example.com", savedUser.get().getEmail());

        // Step 2: Sign In
        SignInRequest signInRequest = new SignInRequest("valid-firebase-token");
        when(firebaseAuthService.signIn("valid-firebase-token")).thenReturn(mockSignInResponse);

        SignInResponse signInResponse = userService.signIn(signInRequest);
        assertNotNull(signInResponse);
        assertEquals("test@example.com", signInResponse.getEmail());

        // Verify last login was updated
        User userAfterSignIn = userRepository.findByEmail("test@example.com").orElseThrow();
        assertNotNull(userAfterSignIn.getLastLoginAt());

        // Step 3: Update User
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .displayName("Updated Name")
                .phoneNumber("+9876543210")
                .emailVerified(true)
                .build();

        UserRecord updatedFirebaseUser = mock(UserRecord.class);
        when(updatedFirebaseUser.getUid()).thenReturn("firebase-uid-123");
        when(updatedFirebaseUser.getEmail()).thenReturn("test@example.com");
        when(updatedFirebaseUser.getDisplayName()).thenReturn("Updated Name");
        when(updatedFirebaseUser.getPhoneNumber()).thenReturn("+9876543210");
        when(updatedFirebaseUser.isEmailVerified()).thenReturn(true);

        when(firebaseAuthService.updateUser(eq("firebase-uid-123"), any(UpdateUserRequest.class)))
                .thenReturn(updatedFirebaseUser);

        UpdateUserResponse updateResponse = userService.updateUser("firebase-uid-123", updateRequest);
        assertNotNull(updateResponse);
        assertEquals("Updated Name", updateResponse.getDisplayName());

        // Verify user was updated in database
        User updatedUser = userRepository.findByEmail("test@example.com").orElseThrow();
        assertEquals("Updated Name", updatedUser.getDisplayName());
        assertEquals("+9876543210", updatedUser.getPhoneNumber());
        assertTrue(updatedUser.getEmailVerified());

        // Step 4: Sign Out
        doNothing().when(firebaseAuthService).signOut("firebase-uid-123");
        assertDoesNotThrow(() -> userService.signOut("firebase-uid-123"));
    }

    @Test
    @DisplayName("Password recovery flow")
    void testPasswordRecoveryFlow() {
        // Given
        PasswordResetRequest request = new PasswordResetRequest("user@example.com");

        doNothing().when(firebaseAuthService).sendPasswordResetEmail("user@example.com");

        // When
        assertDoesNotThrow(() -> userService.recoverPassword(request));

        // Then
        verify(firebaseAuthService, times(1)).sendPasswordResetEmail("user@example.com");
    }

    @Test
    @DisplayName("Sign in should create local user if doesn't exist")
    void testSignIn_CreatesLocalUser() {
        // Given - user exists in Firebase but not in local DB
        String email = "firebase-only@example.com";
        SignInRequest signInRequest = new SignInRequest("valid-token");

        SignInResponse firebaseResponse = SignInResponse.builder()
                .uid("firebase-uid-456")
                .email(email)
                .displayName("Firebase User")
                .emailVerified(true)
                .build();

        when(firebaseAuthService.signIn("valid-token")).thenReturn(firebaseResponse);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        SignInResponse response = userService.signIn(signInRequest);

        // Then
        assertNotNull(response);
        assertEquals(email, response.getEmail());

        // Verify user was created in local database
        Optional<User> localUser = userRepository.findByEmail(email);
        assertTrue(localUser.isPresent());
        assertEquals(email, localUser.get().getEmail());
    }

    @Test
    @DisplayName("Should handle duplicate email on signup")
    void testSignUp_DuplicateEmail() {
        // Given - user already exists
        SignUpRequest request = SignUpRequest.builder()
                .email("existing@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThrows(com.nicecommerce.core.exception.BusinessException.class,
                () -> userService.signUp(request));

        verify(firebaseAuthService, never()).createUser(any());
    }
}

