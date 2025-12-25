package com.nicecommerce.accounts.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Firebase Configuration
 * 
 * Configures Firebase Admin SDK for authentication.
 * Supports both file-based and environment variable configuration.
 * 
 * @author NiceCommerce Team
 */
@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @Value("${firebase.project-id:}")
    private String projectId;

    @Value("${firebase.credentials.json:}")
    private String credentialsJson;

    /**
     * Initialize Firebase App
     */
    @PostConstruct
    public void initializeFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder();

                // Option 1: Use credentials file path
                if (credentialsPath != null && !credentialsPath.isEmpty()) {
                    log.info("Initializing Firebase with credentials file: {}", credentialsPath);
                    InputStream serviceAccount = new FileInputStream(credentialsPath);
                    GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                    optionsBuilder.setCredentials(credentials);
                }
                // Option 2: Use credentials from classpath
                else if (credentialsJson != null && !credentialsJson.isEmpty()) {
                    log.info("Initializing Firebase with credentials from classpath");
                    Resource resource = new ClassPathResource(credentialsJson);
                    InputStream serviceAccount = resource.getInputStream();
                    GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                    optionsBuilder.setCredentials(credentials);
                }
                // Option 3: Use default credentials (for GCP environments)
                else {
                    log.info("Initializing Firebase with default credentials (GCP environment)");
                    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
                    optionsBuilder.setCredentials(credentials);
                }

                // Set project ID if provided
                if (projectId != null && !projectId.isEmpty()) {
                    optionsBuilder.setProjectId(projectId);
                }

                FirebaseOptions options = optionsBuilder.build();
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully");
            } catch (IOException e) {
                log.error("Error initializing Firebase: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to initialize Firebase", e);
            }
        } else {
            log.info("Firebase already initialized");
        }
    }

    /**
     * Firebase Auth Bean
     * 
     * Provides FirebaseAuth instance for authentication operations.
     */
    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}

