package com.nicecommerce.products.entity;

import com.nicecommerce.accounts.entity.User;
import com.nicecommerce.core.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Waitlist Entity
 * 
 * Stores waitlist entries for out-of-stock products.
 * 
 * This is equivalent to Django's products.Waitlist model.
 * 
 * @author NiceCommerce Team
 */
@Entity
@Table(name = "waitlist", uniqueConstraints = {
    @UniqueConstraint(name = "uk_waitlist_product_email_size", 
                     columnNames = {"product_id", "email", "size"})
}, indexes = {
    @Index(name = "idx_waitlist_product_notified", columnList = "product_id,notified"),
    @Index(name = "idx_waitlist_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Waitlist extends BaseEntity {

    /**
     * Product being waitlisted
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * User who joined waitlist (optional - can be anonymous)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Email address for notifications
     */
    @Email
    @NotBlank
    @Column(nullable = false)
    private String email;

    /**
     * Size preference
     */
    @Column(length = 20)
    private String size;

    /**
     * Whether the user has been notified
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean notified = false;

    /**
     * Timestamp when notification was sent
     */
    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;
}

