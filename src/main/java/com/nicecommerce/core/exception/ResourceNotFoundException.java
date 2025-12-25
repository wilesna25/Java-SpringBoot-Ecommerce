package com.nicecommerce.core.exception;

/**
 * Resource Not Found Exception
 * 
 * Thrown when a requested resource is not found.
 * 
 * @author NiceCommerce Team
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

