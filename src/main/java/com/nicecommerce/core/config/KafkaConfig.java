package com.nicecommerce.core.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Configuration
 * 
 * Configures Kafka topics for event streaming.
 * Topics are auto-created if they don't exist.
 * 
 * @author NiceCommerce Team
 */
@Configuration
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;
    
    /**
     * Order events topic
     * Used for order creation, updates, and status changes
     */
    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name("order-events")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    /**
     * Payment events topic
     * Used for payment processing events
     */
    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder.name("payment-events")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    /**
     * Product events topic
     * Used for product updates, stock changes, etc.
     */
    @Bean
    public NewTopic productEventsTopic() {
        return TopicBuilder.name("product-events")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    /**
     * Idempotency tracking topic
     * Used for tracking idempotency keys
     */
    @Bean
    public NewTopic idempotencyTopic() {
        return TopicBuilder.name("idempotency-keys")
            .partitions(3)
            .replicas(1)
            .config("retention.ms", "86400000") // 24 hours
            .build();
    }
}

