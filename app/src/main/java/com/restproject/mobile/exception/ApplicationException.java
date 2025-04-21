package com.restproject.mobile.exception;

public class ApplicationException extends RuntimeException {
    Exception exception;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Exception e) {
        super(message);
        this.exception = e;
    }
}
