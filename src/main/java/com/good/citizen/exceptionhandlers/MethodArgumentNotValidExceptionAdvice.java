package com.good.citizen.exceptionhandlers;

import com.good.citizen.exceptions.ApiExceptionDetails;
import com.good.citizen.exceptions.ApiExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Exception to be thrown when validation on an argument annotated with @Valid fails.
 */

@RestControllerAdvice
public class MethodArgumentNotValidExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodArgumentNotValidExceptionAdvice.class);

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiExceptionResponse> handle(MethodArgumentNotValidException ex) {
        LOGGER.error("Bind exception has occurred", ex);

        var listOfErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new ApiExceptionDetails(error.getCode(), error.getDefaultMessage(), error.getField()))
                .collect(Collectors.toList());

        return ApiExceptionResponse
                .ofBadRequest("One of the input fields contains invalid value", listOfErrors)
                .asResponseEntity();
    }
}