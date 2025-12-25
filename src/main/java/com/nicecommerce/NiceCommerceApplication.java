package com.nicecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot Application Class
 * 
 * This is the entry point for the NiceCommerce Spring Boot application.
 * 
 * @SpringBootApplication: Combines @Configuration, @EnableAutoConfiguration, and @ComponentScan
 * @EnableCaching: Enables Spring's cache abstraction
 * @EnableJpaAuditing: Enables JPA auditing (for @CreatedDate, @LastModifiedDate, etc.)
 * @EnableAsync: Enables asynchronous method execution
 * @EnableScheduling: Enables scheduled task execution
 * 
 * @author NiceCommerce Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class NiceCommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NiceCommerceApplication.class, args);
    }
}

