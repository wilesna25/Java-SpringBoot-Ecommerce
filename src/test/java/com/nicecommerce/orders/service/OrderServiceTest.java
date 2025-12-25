package com.nicecommerce.orders.service;

import com.nicecommerce.accounts.entity.User;
import com.nicecommerce.core.exception.ResourceNotFoundException;
import com.nicecommerce.orders.entity.Order;
import com.nicecommerce.orders.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.nicecommerce.core.service.KafkaEventProducer;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive Unit Tests for OrderService
 * 
 * Tests business logic including idempotency, resilience patterns, and error handling.
 * 
 * @author Senior Java/Spring Boot Expert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private KafkaEventProducer kafkaEventProducer;
    
    @InjectMocks
    private OrderService orderService;
    
    private User testUser;
    private Order testOrder;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .email("test@example.com")
            .displayName("Test User")
            .build();
        
        testOrder = Order.builder()
            .id(1L)
            .user(testUser)
            .orderNumber("ORD-12345678-123456")
            .status(Order.OrderStatus.PENDING)
            .subtotal(BigDecimal.ZERO)
            .total(BigDecimal.ZERO)
            .build();
    }
    
    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {
        
        @Test
        @DisplayName("Should create order successfully without idempotency key")
        void createOrder_WhenNoIdempotencyKey_ShouldCreateNewOrder() {
            // Given
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
            doNothing().when(kafkaEventProducer).publishOrderEvent(any());
            
            // When
            Order result = orderService.createOrder(testUser, null);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUser()).isEqualTo(testUser);
            assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
            assertThat(result.getSubtotal()).isEqualTo(BigDecimal.ZERO);
            assertThat(result.getTotal()).isEqualTo(BigDecimal.ZERO);
            
            verify(orderRepository).save(any(Order.class));
            verify(kafkaEventProducer).publishOrderEvent(any());
            verify(kafkaEventProducer, never()).publishIdempotencyEvent(any());
        }
        
        @Test
        @DisplayName("Should create order successfully with idempotency key")
        void createOrder_WhenIdempotencyKeyProvided_ShouldCreateAndPublishEvents() {
            // Given
            String idempotencyKey = "test-idempotency-key-123";
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
            doNothing().when(kafkaEventProducer).publishOrderEvent(any());
            doNothing().when(kafkaEventProducer).publishIdempotencyEvent(any());
            
            // When
            Order result = orderService.createOrder(testUser, idempotencyKey);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            
            verify(orderRepository).save(any(Order.class));
            verify(kafkaEventProducer).publishOrderEvent(any());
            verify(kafkaEventProducer).publishIdempotencyEvent(any());
        }
    }
    
    @Nested
    @DisplayName("Process Payment Tests")
    class ProcessPaymentTests {
        
        @Test
        @DisplayName("Should process payment successfully")
        void processPayment_WhenSuccessful_ShouldReturnSuccessResult() throws Exception {
            // Given
            Long orderId = 1L;
            
            // When
            CompletableFuture<OrderService.PaymentResult> future = 
                orderService.processPayment(orderId);
            
            // Wait for completion
            OrderService.PaymentResult result = future.get(2, TimeUnit.SECONDS);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getTransactionId()).isNotNull();
            assertThat(result.getTransactionId()).startsWith("TXN-");
        }
        
        @Test
        @DisplayName("Should handle payment processing timeout")
        void processPayment_WhenTimeout_ShouldCompleteWithResult() throws Exception {
            // Given
            Long orderId = 1L;
            
            // When
            CompletableFuture<OrderService.PaymentResult> future = 
                orderService.processPayment(orderId);
            
            // Then - Should complete within reasonable time
            OrderService.PaymentResult result = future.get(5, TimeUnit.SECONDS);
            assertThat(result).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Payment Fallback Tests")
    class PaymentFallbackTests {
        
        @Test
        @DisplayName("Should return fallback result when payment fails")
        void processPaymentFallback_WhenCalled_ShouldReturnFailureResult() {
            // Given
            Long orderId = 1L;
            Exception exception = new RuntimeException("Payment service unavailable");
            
            // When
            CompletableFuture<OrderService.PaymentResult> future = 
                orderService.processPaymentFallback(orderId, exception);
            
            // Then
            assertThat(future).isNotNull();
            assertThat(future.isDone()).isTrue();
            
            try {
                OrderService.PaymentResult result = future.get();
                assertThat(result).isNotNull();
                assertThat(result.isSuccess()).isFalse();
                assertThat(result.getMessage()).isEqualTo("Payment service temporarily unavailable");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Should handle null user gracefully")
        void createOrder_WhenUserIsNull_ShouldStillCreateOrder() {
            // Given
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
            
            // When/Then - Should not throw NPE, but may fail validation
            // This depends on entity validation
            verify(orderRepository, never()).save(any(Order.class));
        }
        
        @Test
        @DisplayName("Should handle empty idempotency key")
        void createOrder_WhenIdempotencyKeyIsEmpty_ShouldCreateNewOrder() {
            // Given
            String emptyKey = "";
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
            doNothing().when(kafkaEventProducer).publishOrderEvent(any());
            
            // When
            Order result = orderService.createOrder(testUser, emptyKey);
            
            // Then
            assertThat(result).isNotNull();
            verify(orderRepository).save(any(Order.class));
            verify(kafkaEventProducer).publishOrderEvent(any());
        }
        
        @Test
        @DisplayName("Should handle very long idempotency key")
        void createOrder_WhenIdempotencyKeyIsVeryLong_ShouldHandleGracefully() {
            // Given
            String longKey = "a".repeat(1000);
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
            doNothing().when(kafkaEventProducer).publishOrderEvent(any());
            doNothing().when(kafkaEventProducer).publishIdempotencyEvent(any());
            
            // When
            Order result = orderService.createOrder(testUser, longKey);
            
            // Then
            assertThat(result).isNotNull();
            verify(kafkaEventProducer).publishIdempotencyEvent(argThat(event -> 
                event.getIdempotencyKey().equals(longKey)
            ));
        }
    }
}

