package com.nicecommerce.core.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Idempotency Event
 * 
 * Event published to Kafka for tracking idempotency keys.
 * 
 * @author NiceCommerce Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyEvent {
    
    private String idempotencyKey;
    private String resourceType; // e.g., "order", "payment"
    private String resourceId;
    private LocalDateTime timestamp;
    private Long ttlSeconds; // Time to live in seconds
}

