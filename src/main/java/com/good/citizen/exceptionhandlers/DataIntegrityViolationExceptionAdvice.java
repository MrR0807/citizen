package com.good.citizen.exceptionhandlers;

import com.good.citizen.exceptions.ApiExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * When Spring is configured with Hibernate, the exception is thrown in the exception translation layer provided by Spring
 * – SessionFactoryUtils::convertHibernateAccessException.
 * <p>
 * There are three possible Hibernate exceptions that may cause the DataIntegrityViolationException to be thrown:
 * - org.hibernate.exception.ConstraintViolationException
 * - org.hibernate.PropertyValueException
 * - org.hibernate.exception.DataException
 * <p>
 * Source - https://www.baeldung.com/spring-dataIntegrityviolationexception
 */

@RestControllerAdvice
public class DataIntegrityViolationExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataIntegrityViolationExceptionAdvice.class);

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ApiExceptionResponse> handle(DataIntegrityViolationException exception) {
        LOGGER.error("DataIntegrityViolationException", exception);

        return ApiExceptionResponse
                .ofBadRequest("Could not create or update due to invalid request parameter")
                .asResponseEntity();
    }
}