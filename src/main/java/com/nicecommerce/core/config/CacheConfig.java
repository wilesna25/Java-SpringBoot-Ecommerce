package com.nicecommerce.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration
 * 
 * Configures Caffeine in-memory caching with different TTLs for different cache names.
 * Replaces Redis with Caffeine for local caching.
 * 
 * @author NiceCommerce Team
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * Configure Caffeine Cache Manager
     * 
     * @return CacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Product cache - 2 hours TTL, max 1000 entries
        cacheManager.registerCustomCache("products", Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .recordStats()
            .build());
        
        // Category cache - 24 hours TTL, max 100 entries (categories change infrequently)
        cacheManager.registerCustomCache("categories", Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .recordStats()
            .build());
        
        // User cache - 30 minutes TTL, max 500 entries
        cacheManager.registerCustomCache("users", Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .recordStats()
            .build());
        
        // Default cache configuration
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats());
        
        return cacheManager;
    }
}

