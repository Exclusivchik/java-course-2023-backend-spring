package edu.java.exceptions;

import lombok.Getter;

@Getter
public class ScrapperInvalidRequestException extends RuntimeException {
    private final String description;

    public ScrapperInvalidRequestException(String message, String description) {
        super(message);
        this.description = description;
    }
}
