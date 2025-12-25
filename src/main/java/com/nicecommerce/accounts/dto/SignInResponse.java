package com.nicecommerce.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Sign In Response DTO
 * 
 * Contains user information after successful Firebase sign in.
 * 
 * @author NiceCommerce Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {
    private String uid;
    private String email;
    private String displayName;
    private Boolean emailVerified;
    private String phoneNumber;
    private String photoUrl;
    private Map<String, Object> customClaims;
}

