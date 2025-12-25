package com.nicecommerce.core.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Google Cloud Platform Configuration
 * 
 * Configures GCP services like Cloud Storage.
 * Only active when 'prod' profile is enabled.
 * 
 * @author NiceCommerce Team
 */
@Configuration
@Profile("prod")
@Slf4j
public class GcpConfig {

    @Value("${gcp.project-id}")
    private String projectId;

    /**
     * Cloud Storage Bean
     * 
     * Provides access to Google Cloud Storage for file operations.
     */
    @Bean
    public Storage storage() {
        log.info("Initializing Google Cloud Storage for project: {}", projectId);
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
    }
}

