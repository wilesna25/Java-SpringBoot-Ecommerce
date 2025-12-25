package com.nicecommerce.core.exception;

/**
 * Unauthorized Exception
 * 
 * Thrown when user is not authorized to perform an action.
 * 
 * @author NiceCommerce Team
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

