package com.good.citizen.exceptionhandlers;

import com.good.citizen.exceptions.ApiExceptionResponse;
import com.good.citizen.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Advice to cover custom Exception.
 *
 * @see com.good.citizen.exceptions.NotFoundException
 */

@RestControllerAdvice
public class NotFoundExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotFoundExceptionAdvice.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handlerMethod(NotFoundException ex) {
        LOGGER.error("Handling NotFoundException", ex);

        return ApiExceptionResponse
                .ofNotFound(ex.getMessage())
                .asResponseEntity();
    }
}