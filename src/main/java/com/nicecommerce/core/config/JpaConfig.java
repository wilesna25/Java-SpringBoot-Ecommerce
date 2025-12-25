package com.nicecommerce.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA Configuration
 * 
 * Enables JPA auditing and repository scanning.
 * 
 * @author NiceCommerce Team
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.nicecommerce")
public class JpaConfig {
    // JPA configuration is handled by annotations
}

