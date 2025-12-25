package com.nicecommerce.payments.entity;

import com.nicecommerce.core.model.BaseEntity;
import com.nicecommerce.orders.entity.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Payment Entity
 * 
 * Represents a payment transaction.
 * 
 * This is equivalent to Django's payments.Payment model.
 * 
 * @author NiceCommerce Team
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_order", columnList = "order_id"),
    @Index(name = "idx_payment_id", columnList = "payment_id"),
    @Index(name = "idx_payment_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    /**
     * Associated order
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Payment ID from payment gateway (unique)
     */
    @Column(name = "payment_id", unique = true, nullable = false, length = 255)
    private String paymentId;

    /**
     * Payment status
     */
    @Column(nullable = false, length = 50)
    private String status;

    /**
     * Payment amount
     */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Currency code (e.g., ARS, USD)
     */
    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "ARS";

    /**
     * Payment method
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * Raw response data from payment gateway (stored as JSON)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_data", columnDefinition = "JSON")
    @Builder.Default
    private Map<String, Object> rawData = new HashMap<>();
}

