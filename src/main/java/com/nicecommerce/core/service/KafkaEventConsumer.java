package com.nicecommerce.core.service;

import com.nicecommerce.core.event.IdempotencyEvent;
import com.nicecommerce.core.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Kafka Event Consumer
 * 
 * Service for consuming events from Kafka topics.
 * 
 * @author NiceCommerce Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {
    
    /**
     * Consume order events
     */
    @KafkaListener(
        topics = "order-events",
        groupId = "nicecommerce-order-consumer",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.info("Received order event - orderId: {}, eventType: {}, partition: {}, offset: {}", 
            event.getOrderId(), event.getEventType(), partition, offset);
        
        try {
            // Process order event
            processOrderEvent(event);
            
            // Acknowledge message
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        } catch (Exception e) {
            log.error("Error processing order event - orderId: {}, error: {}", 
                event.getOrderId(), e.getMessage(), e);
            // In production, implement retry logic or dead letter queue
        }
    }
    
    /**
     * Consume idempotency events
     */
    @KafkaListener(
        topics = "idempotency-keys",
        groupId = "nicecommerce-idempotency-consumer",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeIdempotencyEvent(
            @Payload IdempotencyEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.debug("Received idempotency event - key: {}, resourceId: {}, partition: {}, offset: {}", 
            event.getIdempotencyKey(), event.getResourceId(), partition, offset);
        
        try {
            // Process idempotency event (e.g., store in database for lookup)
            processIdempotencyEvent(event);
            
            // Acknowledge message
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        } catch (Exception e) {
            log.error("Error processing idempotency event - key: {}, error: {}", 
                event.getIdempotencyKey(), e.getMessage(), e);
        }
    }
    
    /**
     * Process order event
     */
    private void processOrderEvent(OrderEvent event) {
        // Implement business logic based on event type
        switch (event.getEventType()) {
            case ORDER_CREATED:
                log.info("Processing ORDER_CREATED event - orderId: {}", event.getOrderId());
                // Send notification, update inventory, etc.
                break;
            case ORDER_PAID:
                log.info("Processing ORDER_PAID event - orderId: {}", event.getOrderId());
                // Trigger shipping, send confirmation, etc.
                break;
            case ORDER_SHIPPED:
                log.info("Processing ORDER_SHIPPED event - orderId: {}", event.getOrderId());
                // Send tracking information, etc.
                break;
            default:
                log.debug("Processing order event - orderId: {}, eventType: {}", 
                    event.getOrderId(), event.getEventType());
        }
    }
    
    /**
     * Process idempotency event
     */
    private void processIdempotencyEvent(IdempotencyEvent event) {
        // Store idempotency key mapping in database or cache
        // This allows checking if a request with this key was already processed
        log.debug("Storing idempotency mapping - key: {}, resourceType: {}, resourceId: {}", 
            event.getIdempotencyKey(), event.getResourceType(), event.getResourceId());
    }
}

