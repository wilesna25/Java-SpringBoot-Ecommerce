package com.nicecommerce.core.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience Configuration
 * 
 * Configures circuit breakers, retries, and timeouts for external service calls.
 * 
 * @author NiceCommerce Team
 */
@Configuration
public class ResilienceConfig {
    
    /**
     * Circuit Breaker Registry
     * 
     * Configures circuit breakers for external services.
     * Circuit breaker opens after 50% failure rate in a sliding window.
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)  // Open circuit after 50% failures
            .waitDurationInOpenState(Duration.ofSeconds(30))  // Wait 30s before trying again
            .slidingWindowSize(10)  // Last 10 calls
            .minimumNumberOfCalls(5)  // Need at least 5 calls before opening
            .permittedNumberOfCallsInHalfOpenState(3)  // Test with 3 calls when half-open
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .build();
        
        return CircuitBreakerRegistry.of(config);
    }
    
    /**
     * Retry Registry
     * 
     * Configures retry logic for transient failures.
     */
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)  // Retry up to 3 times
            .waitDuration(Duration.ofSeconds(1))  // Wait 1s between retries
            .retryExceptions(
                java.io.IOException.class,
                java.util.concurrent.TimeoutException.class,
                org.springframework.web.client.ResourceAccessException.class
            )
            .build();
        
        return RetryRegistry.of(config);
    }
    
    /**
     * Time Limiter Config
     * 
     * Configures timeouts for async operations.
     */
    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(5))
            .cancelRunningFuture(true)
            .build();
    }
}

