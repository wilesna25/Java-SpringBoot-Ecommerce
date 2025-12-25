package com.nicecommerce.core.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order Event
 * 
 * Event published to Kafka when order-related actions occur.
 * 
 * @author NiceCommerce Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    
    /**
     * Event type
     */
    public enum EventType {
        ORDER_CREATED,
        ORDER_UPDATED,
        ORDER_CANCELLED,
        ORDER_PAID,
        ORDER_SHIPPED,
        ORDER_DELIVERED
    }
    
    private EventType eventType;
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String idempotencyKey;
    private String status; // Order status as string
    private BigDecimal total;
    private LocalDateTime timestamp;
    private String metadata; // JSON string for additional data
}

