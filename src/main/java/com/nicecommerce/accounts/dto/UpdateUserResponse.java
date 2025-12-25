package com.nicecommerce.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update User Response DTO
 * 
 * Contains updated user information.
 * 
 * @author NiceCommerce Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponse {
    private String uid;
    private String email;
    private String displayName;
    private String phoneNumber;
    private Boolean emailVerified;
}

