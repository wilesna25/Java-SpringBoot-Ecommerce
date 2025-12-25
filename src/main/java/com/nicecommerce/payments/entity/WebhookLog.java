package com.nicecommerce.payments.entity;

import com.nicecommerce.core.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Webhook Log Entity
 * 
 * Stores webhook requests for debugging and auditing.
 * 
 * This is equivalent to Django's payments.WebhookLog model.
 * 
 * @author NiceCommerce Team
 */
@Entity
@Table(name = "webhook_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookLog extends BaseEntity {

    /**
     * Event type (e.g., "payment.created", "payment.updated")
     */
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    /**
     * Webhook payload (stored as JSON)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    @Builder.Default
    private Map<String, Object> payload = new HashMap<>();

    /**
     * Whether the webhook was processed successfully
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean processed = false;

    /**
     * Error message if processing failed
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}

