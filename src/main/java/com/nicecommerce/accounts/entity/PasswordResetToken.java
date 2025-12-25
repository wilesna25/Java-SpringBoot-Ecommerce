package com.nicecommerce.accounts.entity;

import com.nicecommerce.core.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Password Reset Token Entity
 * 
 * Stores password reset tokens for users.
 * Tokens expire after a certain period and can only be used once.
 * 
 * This is equivalent to Django's accounts.PasswordResetToken model.
 * 
 * @author NiceCommerce Team
 */
@Entity
@Table(name = "password_reset_tokens", indexes = {
    @Index(name = "idx_reset_token", columnList = "token"),
    @Index(name = "idx_reset_expires", columnList = "expires_at"),
    @Index(name = "idx_reset_used", columnList = "used")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken extends BaseEntity {

    /**
     * User who requested the password reset
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Unique token string
     */
    @Column(unique = true, nullable = false, length = 64)
    private String token;

    /**
     * Expiration timestamp
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Whether the token has been used
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean used = false;

    /**
     * Check if token is still valid
     */
    public boolean isValid() {
        return !used && LocalDateTime.now().isBefore(expiresAt);
    }
}

