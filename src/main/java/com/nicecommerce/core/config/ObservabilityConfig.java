package com.nicecommerce.core.config;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Observability Configuration
 * 
 * Configures metrics collection, distributed tracing, and structured logging.
 * 
 * @author NiceCommerce Team
 */
@Configuration
public class ObservabilityConfig {
    
    /**
     * Configure common tags for all metrics
     * 
     * @return MeterRegistryCustomizer
     */
    @Bean
    public io.micrometer.core.instrument.config.MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
            .commonTags("application", "nicecommerce-api")
            .commonTags("environment", 
                System.getProperty("spring.profiles.active", "dev"));
    }
    
    /**
     * Enable @Timed annotation support
     * 
     * @param registry MeterRegistry
     * @return TimedAspect
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    
    /**
     * Enable @Counted annotation support
     * 
     * @param registry MeterRegistry
     * @return CountedAspect
     */
    @Bean
    public CountedAspect countedAspect(MeterRegistry registry) {
        return new CountedAspect(registry);
    }
    
    /**
     * Configure meter filters
     * 
     * @return MeterFilter
     */
    @Bean
    public MeterFilter meterFilter() {
        return MeterFilter.denyNameStartsWith("jvm.threads");
    }
}

