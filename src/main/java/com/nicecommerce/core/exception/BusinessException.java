package com.nicecommerce.core.exception;

/**
 * Business Exception
 * 
 * Thrown when business logic validation fails.
 * 
 * @author NiceCommerce Team
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

