package com.nicecommerce.accounts.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test Configuration
 * 
 * Provides test-specific beans and configurations.
 * 
 * @author NiceCommerce Team
 */
@TestConfiguration
@Profile("test")
public class TestConfig {
    // Test-specific configurations can be added here
    // Firebase is mocked using @MockBean in tests
}

