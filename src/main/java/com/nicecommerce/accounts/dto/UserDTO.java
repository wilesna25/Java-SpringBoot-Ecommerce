package com.nicecommerce.accounts.dto;

import com.nicecommerce.accounts.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Data Transfer Object
 * 
 * DTO for transferring user data without exposing sensitive information.
 * 
 * @author NiceCommerce Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String displayName;
    private String phoneNumber;
    private Boolean emailVerified;
    private User.UserRole role;
    private User.StaffRole staffRole;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Note: password is never included in DTOs
}

