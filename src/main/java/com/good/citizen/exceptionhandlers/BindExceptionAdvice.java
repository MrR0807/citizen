package com.good.citizen.exceptionhandlers;

import com.good.citizen.exceptions.ApiExceptionDetails;
import com.good.citizen.exceptions.ApiExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Bind exception happens when Spring cannot bind parameters with model.
 * Say, for example, EmployeeFilter would have jakarta constraint on ``team`` property - ``@NotNull private final String team``.
 * If request would not contain team variable, BindException would be thrown.
 *
 * @see com.good.citizen.employees.api.request.EmployeeFilter
 * @see com.good.citizen.employees.api.EmployeesEndPoint
 */

@RestControllerAdvice
public class BindExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(BindExceptionAdvice.class);

    @ExceptionHandler({BindException.class})
    public ResponseEntity<ApiExceptionResponse> handle(BindException ex) {
        LOGGER.error("Bind exception has occurred", ex);

        var listOfErrors = ex.getFieldErrors()
                .stream()
                .map(error -> new ApiExceptionDetails(error.getCode(), error.getDefaultMessage(), error.getField()))
                .collect(Collectors.toList());

        return ApiExceptionResponse
                .ofBadRequest("One of the input fields contains invalid value", listOfErrors)
                .asResponseEntity();
    }
}