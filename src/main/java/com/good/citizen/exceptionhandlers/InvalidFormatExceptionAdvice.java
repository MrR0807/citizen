package com.good.citizen.exceptionhandlers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.good.citizen.exceptions.ApiExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

/**
 * Specialized sub-class of JsonMappingException that is used when the underlying problem appears to be that of bad formatting of a value to deserialize.
 * Most of the times this exception is thrown when request contains bad date format.
 */

@RestControllerAdvice
public class InvalidFormatExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidFormatExceptionAdvice.class);

    @ExceptionHandler({InvalidFormatException.class})
    public ResponseEntity<ApiExceptionResponse> handle(InvalidFormatException exception) {
        LOGGER.error("InvalidFormatException exception has occurred", exception);

        var responseErrorMessage = new StringBuilder("Failed to deserialize. ");
        if (exception.getCause() instanceof DateTimeParseException) {
            responseErrorMessage.append("Wrong date format. Use YYYY-MM-DD.");
        }

        return ApiExceptionResponse
                .ofBadRequest(responseErrorMessage.toString())
                .asResponseEntity();
    }
}