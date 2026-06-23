package com.example.ems_common.exceptions;

public class EventAlreadyEndedException extends RuntimeException {
    public EventAlreadyEndedException(String message) {
        super(message);
    }
}
