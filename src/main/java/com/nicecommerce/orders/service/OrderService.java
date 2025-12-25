package com.nicecommerce.orders.service;

import com.nicecommerce.core.event.IdempotencyEvent;
import com.nicecommerce.core.event.OrderEvent;
import com.nicecommerce.core.exception.BusinessException;
import com.nicecommerce.core.exception.ResourceNotFoundException;
import com.nicecommerce.core.service.KafkaEventProducer;
import com.nicecommerce.orders.entity.Order;
import com.nicecommerce.orders.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Order Service
 * 
 * Business logic for order operations.
 * Implements idempotency, resilience patterns, and best practices.
 * 
 * @author NiceCommerce Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final KafkaEventProducer kafkaEventProducer;
    
    /**
     * Create order with idempotency support using Kafka
     * 
     * @param user User entity
     * @param idempotencyKey Idempotency key to prevent duplicate orders
     * @return Order
     */
    @Transactional
    public Order createOrder(com.nicecommerce.accounts.entity.User user, String idempotencyKey) {
        // Note: Idempotency check should be done before calling this method
        // by checking the idempotency-keys topic or a database table
        
        // Create new order
        Order order = Order.builder()
            .user(user)
            .status(Order.OrderStatus.PENDING)
            .subtotal(java.math.BigDecimal.ZERO)
            .total(java.math.BigDecimal.ZERO)
            .build();
        order = orderRepository.save(order);
        
        // Publish order created event to Kafka
        OrderEvent orderEvent = OrderEvent.builder()
            .eventType(OrderEvent.EventType.ORDER_CREATED)
            .orderId(order.getId())
            .orderNumber(order.getOrderNumber())
            .userId(user.getId())
            .idempotencyKey(idempotencyKey)
            .status(order.getStatus().name()) // Convert enum to string
            .total(order.getTotal())
            .timestamp(LocalDateTime.now())
            .build();
        
        kafkaEventProducer.publishOrderEvent(orderEvent);
        
        // Publish idempotency event if key provided
        if (idempotencyKey != null) {
            IdempotencyEvent idempotencyEvent = IdempotencyEvent.builder()
                .idempotencyKey(idempotencyKey)
                .resourceType("order")
                .resourceId(order.getId().toString())
                .timestamp(LocalDateTime.now())
                .ttlSeconds(86400L) // 24 hours
                .build();
            
            kafkaEventProducer.publishIdempotencyEvent(idempotencyEvent);
        }
        
        log.info("Order created successfully - orderId: {}, userId: {}", 
            order.getId(), user.getId());
        
        return order;
    }
    
    /**
     * Process payment with circuit breaker and retry
     * 
     * @param orderId Order ID
     * @return CompletableFuture<PaymentResult>
     */
    @CircuitBreaker(name = "payment-service", fallbackMethod = "processPaymentFallback")
    @Retry(name = "payment-service")
    @TimeLimiter(name = "payment-service")
    public CompletableFuture<PaymentResult> processPayment(Long orderId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Processing payment for order - orderId: {}", orderId);
            
            // Simulate payment processing
            // In real implementation, this would call external payment service
            
            try {
                Thread.sleep(1000);  // Simulate network delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return PaymentResult.builder()
                .success(true)
                .transactionId("TXN-" + System.currentTimeMillis())
                .build();
        });
    }
    
    /**
     * Fallback method for payment processing
     * 
     * @param orderId Order ID
     * @param e Exception
     * @return PaymentResult
     */
    private CompletableFuture<PaymentResult> processPaymentFallback(
            Long orderId, Exception e) {
        log.error("Payment processing failed - using fallback - orderId: {}, error: {}", 
            orderId, e.getMessage(), e);
        
        return CompletableFuture.completedFuture(
            PaymentResult.builder()
                .success(false)
                .message("Payment service temporarily unavailable")
                .build());
    }
    
    /**
     * Payment Result DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class PaymentResult {
        private boolean success;
        private String transactionId;
        private String message;
    }
}

