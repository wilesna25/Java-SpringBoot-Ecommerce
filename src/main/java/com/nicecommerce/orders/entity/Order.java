package com.nicecommerce.orders.entity;

import com.nicecommerce.accounts.entity.User;
import com.nicecommerce.core.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Order Entity
 * 
 * Represents an order in the system.
 * 
 * This is equivalent to Django's orders.Order model.
 * 
 * @author NiceCommerce Team
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_user_status", columnList = "user_id,status"),
    @Index(name = "idx_order_number", columnList = "order_number"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_payment", columnList = "payment_id"),
    @Index(name = "idx_order_created", columnList = "created_at"),
    @Index(name = "idx_order_pending", columnList = "pending_timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    /**
     * User who placed the order
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Unique order number
     */
    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    /**
     * Order status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Order items (stored as JSON)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Subtotal amount
     */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * Shipping cost
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shipping = BigDecimal.ZERO;

    /**
     * Tax amount
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;

    /**
     * Total amount
     */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    /**
     * Shipping address (stored as JSON)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "shipping_address", columnDefinition = "JSON")
    @Builder.Default
    private Map<String, String> shippingAddress = new HashMap<>();

    /**
     * Payment ID from payment gateway
     */
    @Column(name = "payment_id", length = 255)
    private String paymentId;

    /**
     * Payment method
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * Tracking number
     */
    @Column(name = "tracking_number", length = 255)
    private String trackingNumber;

    /**
     * Shipping timestamp
     */
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    /**
     * Delivery timestamp
     */
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    /**
     * Reason for pending status
     */
    @Column(name = "pending_reason", columnDefinition = "TEXT")
    private String pendingReason;

    /**
     * Timestamp when order entered pending status
     */
    @Column(name = "pending_timestamp")
    private LocalDateTime pendingTimestamp;

    /**
     * Admin notes
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Staff notes
     */
    @Column(name = "staff_notes", columnDefinition = "TEXT")
    private String staffNotes;

    /**
     * Staff member assigned to this order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    /**
     * Order Status Enumeration
     */
    public enum OrderStatus {
        PENDING,
        PAID,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED
    }

    /**
     * Inner class representing an order item
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private Long productId;
        private String productName;
        private String size;
        private Integer quantity;
        private String price;
        private String imageUrl;
    }

    /**
     * Generate unique order number
     */
    @PrePersist
    public void generateOrderNumber() {
        if (orderNumber == null || orderNumber.isBlank()) {
            long timestamp = System.currentTimeMillis();
            String randomPart = String.valueOf((int)(Math.random() * 1000000));
            orderNumber = String.format("ORD-%08d-%s", 
                timestamp % 100000000, 
                randomPart);
        }
        
        // Track pending status
        if (status == OrderStatus.PENDING && pendingTimestamp == null) {
            pendingTimestamp = LocalDateTime.now();
        }
    }

    /**
     * Check if order is paid
     */
    public boolean isPaid() {
        return status == OrderStatus.PAID ||
               status == OrderStatus.PROCESSING ||
               status == OrderStatus.SHIPPED ||
               status == OrderStatus.DELIVERED;
    }
}

