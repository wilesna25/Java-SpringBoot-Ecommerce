package com.nicecommerce.accounts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sign In Request DTO
 * 
 * Used for Firebase sign in with ID token.
 * 
 * @author NiceCommerce Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    
    @NotBlank(message = "ID token is required")
    private String idToken;
}

