package com.fran.threads.exception;

public class CounterTaskNotFoundException extends RuntimeException {
    public CounterTaskNotFoundException(String message) {
        super(message);
    }
}
