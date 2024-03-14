package edu.java.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String description;
    private final String message;

    public NotFoundException(String message, String description) {
        this.message = message;
        this.description = description;
    }
}
