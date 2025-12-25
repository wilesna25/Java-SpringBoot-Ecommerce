package com.nicecommerce.accounts.controller;

import com.nicecommerce.accounts.dto.*;
import com.nicecommerce.accounts.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * 
 * REST API endpoints for Firebase authentication:
 * - Sign up (create user)
 * - Sign in (verify token)
 * - Sign out (revoke tokens)
 * - Update user
 * - Password recovery
 * 
 * @author NiceCommerce Team
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * User registration endpoint
     * Creates user in Firebase and local database
     * 
     * POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@Valid @RequestBody SignUpRequest request) {
        UserDTO response = userService.signUp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * User sign in endpoint
     * Verifies Firebase ID token and returns user information
     * 
     * POST /api/auth/signin
     */
    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest request) {
        SignInResponse response = userService.signIn(request);
        return ResponseEntity.ok(response);
    }

    /**
     * User sign out endpoint
     * Revokes all refresh tokens for the user
     * 
     * POST /api/auth/signout/{uid}
     */
    @PostMapping("/signout/{uid}")
    public ResponseEntity<Void> signOut(@PathVariable String uid) {
        userService.signOut(uid);
        return ResponseEntity.ok().build();
    }

    /**
     * Update user endpoint
     * Updates user information in Firebase and local database
     * 
     * PUT /api/auth/users/{uid}
     */
    @PutMapping("/users/{uid}")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @PathVariable String uid,
            @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserResponse response = userService.updateUser(uid, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Password recovery endpoint
     * Sends password reset email
     * 
     * POST /api/auth/password/reset
     */
    @PostMapping("/password/reset")
    public ResponseEntity<Void> recoverPassword(@Valid @RequestBody PasswordResetRequest request) {
        userService.recoverPassword(request);
        return ResponseEntity.ok().build();
    }
}

