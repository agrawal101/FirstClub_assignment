package com.firstclub.membership.exception;

/**
 * Thrown when an action violates a membership business rule
 * (e.g. subscribing while an active subscription already exists). Maps to HTTP 409.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}