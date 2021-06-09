package com.trustly.challenge.exception;

/**
 * Generic Exception for mapping errors in the application.
 */
public class BusinessException extends Exception{

    public BusinessException(String message) {
        super(message);
    }
}
