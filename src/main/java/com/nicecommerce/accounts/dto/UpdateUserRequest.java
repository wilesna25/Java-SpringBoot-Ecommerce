package com.nicecommerce.accounts.dto;

import com.nicecommerce.accounts.entity.User;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update User Request DTO
 * 
 * Used for updating user information in Firebase.
 * All fields are optional - only provided fields will be updated.
 * 
 * @author NiceCommerce Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String displayName;
    
    private String phoneNumber;
    
    private String password;
    
    private Boolean emailVerified;
    
    private User.UserRole role;
}

