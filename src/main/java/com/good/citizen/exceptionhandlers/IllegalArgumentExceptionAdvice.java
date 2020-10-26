package com.good.citizen.exceptionhandlers;

import com.good.citizen.exceptions.ApiExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Advice to cover Java's standard IllegalArgumentException.
 */

@RestControllerAdvice
public class IllegalArgumentExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(IllegalArgumentExceptionAdvice.class);

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiExceptionResponse> handle(IllegalArgumentException exception) {
        LOGGER.error("IllegalArgumentException", exception);

        return ApiExceptionResponse
                .ofBadRequest(exception.getMessage())
                .asResponseEntity();
    }
}