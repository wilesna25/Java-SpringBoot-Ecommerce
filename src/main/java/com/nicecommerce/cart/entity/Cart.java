package com.nicecommerce.cart.entity;

import com.nicecommerce.accounts.entity.User;
import com.nicecommerce.core.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart Entity
 * 
 * Represents a shopping cart. Supports both authenticated users
 * and anonymous users (via session key).
 * 
 * This is equivalent to Django's cart.Cart model.
 * 
 * @author NiceCommerce Team
 */
@Entity
@Table(name = "carts", uniqueConstraints = {
    @UniqueConstraint(name = "uk_cart_user_session", 
                     columnNames = {"user_id", "session_key"})
}, indexes = {
    @Index(name = "idx_cart_user", columnList = "user_id"),
    @Index(name = "idx_cart_session", columnList = "session_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart extends BaseEntity {

    /**
     * User who owns the cart (null for anonymous users)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Session key for anonymous users
     */
    @Column(name = "session_key", length = 40)
    private String sessionKey;

    /**
     * Cart items (stored as JSON)
     * Example: [{"productId": 1, "size": "M", "quantity": 2}]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    /**
     * Inner class representing a cart item
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItem {
        private Long productId;
        private String size;
        private Integer quantity;
    }

    /**
     * Calculate total price of all items in cart
     * Note: This requires fetching products, so it's better to calculate in service layer
     */
    public int getItemCount() {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        return items.stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 1)
                .sum();
    }

    /**
     * Add item to cart
     */
    public void addItem(Long productId, String size, Integer quantity) {
        if (items == null) {
            items = new ArrayList<>();
        }
        
        // Check if item already exists
        for (CartItem item : items) {
            if (item.getProductId().equals(productId) && 
                size.equals(item.getSize())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        
        // Add new item
        items.add(new CartItem(productId, size, quantity));
    }

    /**
     * Remove item from cart
     */
    public void removeItem(Long productId, String size) {
        if (items == null) {
            return;
        }
        items.removeIf(item -> 
            item.getProductId().equals(productId) && 
            size.equals(item.getSize())
        );
    }

    /**
     * Update item quantity
     */
    public void updateItemQuantity(Long productId, String size, Integer quantity) {
        if (items == null) {
            return;
        }
        
        for (CartItem item : items) {
            if (item.getProductId().equals(productId) && 
                size.equals(item.getSize())) {
                if (quantity <= 0) {
                    removeItem(productId, size);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    /**
     * Clear all items from cart
     */
    public void clear() {
        if (items != null) {
            items.clear();
        }
    }
}

