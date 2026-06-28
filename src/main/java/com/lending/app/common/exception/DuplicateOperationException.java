package com.lending.app.common.exception;

public class DuplicateOperationException extends RuntimeException {

    public DuplicateOperationException(String message) {
        super(message);
    }
}
