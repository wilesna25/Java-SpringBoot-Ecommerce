package com.nicecommerce.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicecommerce.accounts.dto.*;
import com.nicecommerce.accounts.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for AuthController
 * 
 * Tests REST API endpoints with mocked service layer.
 * 
 * @author NiceCommerce Team
 */
@WebMvcTest(AuthController.class)
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/signup - Should create user successfully")
    void testSignUp_Success() throws Exception {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("newuser@example.com")
                .password("password123")
                .displayName("New User")
                .build();

        UserDTO response = UserDTO.builder()
                .id(1L)
                .email("newuser@example.com")
                .displayName("New User")
                .emailVerified(false)
                .build();

        when(userService.signUp(any(SignUpRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.displayName").value("New User"));

        verify(userService, times(1)).signUp(any(SignUpRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/signup - Should return 400 for invalid request")
    void testSignUp_InvalidRequest() throws Exception {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("invalid-email") // Invalid email format
                .password("123") // Too short
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).signUp(any());
    }

    @Test
    @DisplayName("POST /api/auth/signin - Should sign in user successfully")
    void testSignIn_Success() throws Exception {
        // Given
        SignInRequest request = new SignInRequest("valid-firebase-token");

        SignInResponse response = SignInResponse.builder()
                .uid("firebase-uid-123")
                .email("user@example.com")
                .displayName("Test User")
                .emailVerified(true)
                .build();

        when(userService.signIn(any(SignInRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("firebase-uid-123"))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(userService, times(1)).signIn(any(SignInRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/signin - Should return 400 for missing token")
    void testSignIn_MissingToken() throws Exception {
        // Given
        SignInRequest request = new SignInRequest(""); // Empty token

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).signIn(any());
    }

    @Test
    @DisplayName("POST /api/auth/signout/{uid} - Should sign out user successfully")
    void testSignOut_Success() throws Exception {
        // Given
        String uid = "firebase-uid-123";

        doNothing().when(userService).signOut(uid);

        // When & Then
        mockMvc.perform(post("/api/auth/signout/{uid}", uid))
                .andExpect(status().isOk());

        verify(userService, times(1)).signOut(uid);
    }

    @Test
    @DisplayName("PUT /api/auth/users/{uid} - Should update user successfully")
    void testUpdateUser_Success() throws Exception {
        // Given
        String uid = "firebase-uid-123";
        UpdateUserRequest request = UpdateUserRequest.builder()
                .displayName("Updated Name")
                .phoneNumber("+9876543210")
                .build();

        UpdateUserResponse response = UpdateUserResponse.builder()
                .uid(uid)
                .email("user@example.com")
                .displayName("Updated Name")
                .phoneNumber("+9876543210")
                .emailVerified(true)
                .build();

        when(userService.updateUser(eq(uid), any(UpdateUserRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/api/auth/users/{uid}", uid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Updated Name"))
                .andExpect(jsonPath("$.phoneNumber").value("+9876543210"));

        verify(userService, times(1)).updateUser(eq(uid), any(UpdateUserRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/password/reset - Should send password reset email")
    void testRecoverPassword_Success() throws Exception {
        // Given
        PasswordResetRequest request = new PasswordResetRequest("user@example.com");

        doNothing().when(userService).recoverPassword(any(PasswordResetRequest.class));

        // When & Then
        mockMvc.perform(post("/api/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService, times(1)).recoverPassword(any(PasswordResetRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/password/reset - Should return 400 for invalid email")
    void testRecoverPassword_InvalidEmail() throws Exception {
        // Given
        PasswordResetRequest request = new PasswordResetRequest("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).recoverPassword(any());
    }
}

