package com.good.citizen.exceptionhandlers;

import com.good.citizen.exceptions.ApiExceptionResponse;
import com.good.citizen.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Advice to cover custom Exception.
 *
 * @see com.good.citizen.exceptions.BadRequestException
 */

@RestControllerAdvice
public class BadRequestExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(BadRequestExceptionAdvice.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiExceptionResponse> handlerMethod(BadRequestException ex) {
        LOGGER.error("Handling BadRequestException", ex);

        return ApiExceptionResponse
                .ofBadRequest(ex.getMessage())
                .asResponseEntity();
    }
}