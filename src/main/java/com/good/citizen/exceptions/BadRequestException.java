package com.good.citizen.exceptions;

import com.good.citizen.shared.Result;

import java.util.List;

public class BadRequestException extends RuntimeException {

    private final List<Result.Error> errors;

    public BadRequestException(String message) {
        super(message);
        this.errors = List.of();
    }

    public BadRequestException(String message, List<Result.Error> errors) {
        super(message);
        this.errors = errors;
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.errors = List.of();
    }

    public List<Result.Error> getErrors() {
        return this.errors;
    }
}