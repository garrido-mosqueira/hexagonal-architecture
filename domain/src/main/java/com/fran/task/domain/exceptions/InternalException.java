package com.fran.task.domain.exceptions;

public class InternalException extends RuntimeException {

    public InternalException(Exception e) {
        super(e);
    }

    public InternalException(String message) {
        super(message);
    }

}
