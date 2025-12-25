package com.nicecommerce.accounts.entity;

import com.nicecommerce.core.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * User Entity
 * 
 * Represents a user in the system. Implements Spring Security's UserDetails
 * for authentication and authorization.
 * 
 * This is equivalent to Django's accounts.User model.
 * 
 * @author NiceCommerce Team
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_firebase_uid", columnList = "firebase_uid"),
    @Index(name = "idx_user_role", columnList = "role"),
    @Index(name = "idx_user_staff_role", columnList = "staff_role"),
    @Index(name = "idx_user_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    /**
     * Firebase UID (unique identifier from Firebase)
     */
    @Column(name = "firebase_uid", unique = true, length = 128)
    private String firebaseUid;

    /**
     * Email address - used as username (unique)
     */
    @Email
    @NotBlank
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    /**
     * Encrypted password (BCrypt)
     */
    @NotBlank
    @Size(min = 8)
    @Column(nullable = false)
    private String password;

    /**
     * Display name for the user
     */
    @Column(name = "display_name", length = 255)
    private String displayName;

    /**
     * Phone number
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * Whether the email has been verified
     */
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * User role: CUSTOMER or ADMIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.CUSTOMER;

    /**
     * Staff role (for admin users)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "staff_role", length = 30)
    private StaffRole staffRole;

    /**
     * Whether the user account is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Whether the user is a staff member
     */
    @Column(name = "is_staff", nullable = false)
    @Builder.Default
    private Boolean isStaff = false;

    /**
     * Whether the user is a superuser
     */
    @Column(name = "is_superuser", nullable = false)
    @Builder.Default
    private Boolean isSuperuser = false;

    /**
     * Last login timestamp
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * User Role Enumeration
     */
    public enum UserRole {
        CUSTOMER,
        ADMIN
    }

    /**
     * Staff Role Enumeration
     */
    public enum StaffRole {
        SUPER_ADMIN,
        SALES_MANAGER,
        SUPPORT_AGENT,
        LOGISTICS_COORDINATOR
    }

    /**
     * Pre-persist callback: Set display name if not provided
     */
    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (displayName == null || displayName.isBlank()) {
            displayName = email != null ? email.split("@")[0] : "User";
        }
        
        // Automatically set isStaff based on role
        if (role == UserRole.ADMIN) {
            isStaff = true;
        } else if (role == UserRole.CUSTOMER && !isSuperuser) {
            isStaff = false;
        }
    }

    /**
     * Check if user is a staff member
     */
    public boolean isStaffMember() {
        return role == UserRole.ADMIN || isStaff || staffRole != null;
    }

    // UserDetails interface implementation

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isSuperuser) {
            return Set.of(new SimpleGrantedAuthority("ROLE_SUPERUSER"));
        }
        if (role == UserRole.ADMIN) {
            return Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}

