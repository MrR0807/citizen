package com.good.citizen.exceptionhandlers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.good.citizen.exceptions.ApiExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * Thrown by HttpMessageConverter implementations when the HttpMessageConverter.read(Class<? extends T>, HttpInputMessage) method fails.
 * The easiest way to trigger this Advice is to pass not existing Enum value.
 * Example, creating equipment via POST:
 * {
 * "jobTitle": "Not Existing Job Title",
 * "lastName": "string",
 * "name": "string",
 * "team": "string"
 * }
 */

@RestControllerAdvice
public class HttpMessageNotReadableExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMessageNotReadableExceptionAdvice.class);

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiExceptionResponse> handle(HttpMessageNotReadableException exception) {
        LOGGER.error("HttpMessageNotReadableException", exception);

        var cause = exception.getRootCause();
        var responseErrorMessage = new StringBuilder("Failed to deserialize.");

        if (Objects.nonNull(cause) && cause instanceof InvalidFormatException e) {
            var target = e.getTargetType();
            if (target.isEnum()) {
                responseErrorMessage.append(" Possible values: ");
                for (Object enumValue : target.getEnumConstants()) {
                    responseErrorMessage.append(enumValue).append(",");
                }
                responseErrorMessage.deleteCharAt(responseErrorMessage.length() - 1);
            }
        }

        return ApiExceptionResponse
                .ofBadRequest(responseErrorMessage.toString())
                .asResponseEntity();
    }
}