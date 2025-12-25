package com.nicecommerce.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI Configuration
 * 
 * Expert-level OpenAPI/Swagger configuration with:
 * - Comprehensive API documentation
 * - Security schemes (Firebase Authentication)
 * - Multiple server environments
 * - Custom info and contact details
 * - Proper component organization
 * 
 * @author NiceCommerce Team
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${spring.application.name:NiceCommerce API}")
    private String applicationName;
    
    @Value("${app.version:1.0.0}")
    private String applicationVersion;
    
    @Value("${app.description:Mobile API for iOS and Android clients}")
    private String applicationDescription;
    
    /**
     * OpenAPI Bean Configuration
     * 
     * Configures the OpenAPI specification with:
     * - API information (title, version, description)
     * - Contact information
     * - License information
     * - Server URLs (production, development, local)
     * - Security schemes (Firebase Bearer Token)
     * - Global security requirements
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(createApiInfo())
            .servers(createServers())
            .components(createComponents())
            .addSecurityItem(createSecurityRequirement());
    }
    
    /**
     * Create API Information
     */
    private Info createApiInfo() {
        return new Info()
            .title(applicationName)
            .version(applicationVersion)
            .description(applicationDescription + "\n\n" +
                "## Features\n" +
                "- ✅ Contract-first API development\n" +
                "- ✅ Firebase Authentication\n" +
                "- ✅ Rate limiting\n" +
                "- ✅ Comprehensive error handling\n" +
                "- ✅ Pagination support\n" +
                "- ✅ Filtering and sorting\n\n" +
                "## Authentication\n" +
                "This API uses Firebase Authentication. Include the Firebase ID token in the Authorization header:\n" +
                "```\n" +
                "Authorization: Bearer <firebase-id-token>\n" +
                "```\n\n" +
                "## Rate Limiting\n" +
                "API requests are rate-limited to prevent abuse:\n" +
                "- 120 requests per minute\n" +
                "- 2000 requests per hour\n" +
                "- 20000 requests per day\n\n" +
                "## Error Responses\n" +
                "All errors follow a consistent format:\n" +
                "```json\n" +
                "{\n" +
                "  \"timestamp\": \"2024-01-15T10:30:00Z\",\n" +
                "  \"status\": 400,\n" +
                "  \"error\": \"Bad Request\",\n" +
                "  \"message\": \"Validation failed\",\n" +
                "  \"path\": \"/api/v1/products\"\n" +
                "}\n" +
                "```")
            .contact(createContact())
            .license(createLicense())
            .termsOfService("https://nicecommerce.com/terms");
    }
    
    /**
     * Create Contact Information
     */
    private Contact createContact() {
        return new Contact()
            .name("API Support Team")
            .email("api-support@nicecommerce.com")
            .url("https://nicecommerce.com/support");
    }
    
    /**
     * Create License Information
     */
    private License createLicense() {
        return new License()
            .name("MIT License")
            .url("https://opensource.org/licenses/MIT");
    }
    
    /**
     * Create Server URLs
     * 
     * Multiple server environments for different stages
     */
    private List<Server> createServers() {
        return List.of(
            new Server()
                .url("https://api.nicecommerce.com/api/v1")
                .description("Production Server")
                .variables(null),
            new Server()
                .url("https://api-dev.nicecommerce.com/api/v1")
                .description("Development Server")
                .variables(null),
            new Server()
                .url("http://localhost:8080/api/v1")
                .description("Local Development Server")
                .variables(null)
        );
    }
    
    /**
     * Create Components
     * 
     * Defines reusable components including security schemes
     */
    private Components createComponents() {
        return new Components()
            .addSecuritySchemes("FirebaseAuth", createFirebaseSecurityScheme())
            .addSecuritySchemes("ApiKey", createApiKeySecurityScheme());
    }
    
    /**
     * Create Firebase Security Scheme
     * 
     * Bearer token authentication using Firebase ID tokens
     */
    private SecurityScheme createFirebaseSecurityScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Firebase ID Token Authentication\n\n" +
                "To authenticate:\n" +
                "1. Obtain a Firebase ID token from your mobile app\n" +
                "2. Include it in the Authorization header:\n" +
                "   `Authorization: Bearer <your-firebase-id-token>`\n\n" +
                "The token will be validated against Firebase Authentication service.");
    }
    
    /**
     * Create API Key Security Scheme
     * 
     * Optional API key for service-to-service communication
     */
    private SecurityScheme createApiKeySecurityScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .in(SecurityScheme.In.HEADER)
            .name("X-API-Key")
            .description("API Key for service-to-service authentication");
    }
    
    /**
     * Create Global Security Requirement
     * 
     * Apply Firebase authentication globally (can be overridden per endpoint)
     */
    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement()
            .addList("FirebaseAuth");
    }
}

