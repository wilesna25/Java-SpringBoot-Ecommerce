package com.nicecommerce.core.service;

import com.nicecommerce.core.event.IdempotencyEvent;
import com.nicecommerce.core.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Event Producer
 * 
 * Service for publishing events to Kafka topics.
 * 
 * @author NiceCommerce Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * Publish order event
     */
    public void publishOrderEvent(OrderEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send("order-events", event.getOrderId().toString(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Order event published successfully - orderId: {}, eventType: {}, offset: {}", 
                        event.getOrderId(), event.getEventType(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish order event - orderId: {}, eventType: {}, error: {}", 
                        event.getOrderId(), event.getEventType(), ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error publishing order event - orderId: {}, error: {}", 
                event.getOrderId(), e.getMessage(), e);
        }
    }
    
    /**
     * Publish idempotency event
     */
    public void publishIdempotencyEvent(IdempotencyEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send("idempotency-keys", event.getIdempotencyKey(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Idempotency event published - key: {}, resourceId: {}", 
                        event.getIdempotencyKey(), event.getResourceId());
                } else {
                    log.error("Failed to publish idempotency event - key: {}, error: {}", 
                        event.getIdempotencyKey(), ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error publishing idempotency event - key: {}, error: {}", 
                event.getIdempotencyKey(), e.getMessage(), e);
        }
    }
}

