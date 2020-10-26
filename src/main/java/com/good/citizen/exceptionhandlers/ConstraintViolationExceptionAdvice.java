package com.good.citizen.exceptionhandlers;

import com.good.citizen.exceptions.ApiExceptionDetails;
import com.good.citizen.exceptions.ApiExceptionResponse;
import com.google.common.base.CaseFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.stream.Collectors;

/**
 * Hibernate ConstraintViolationException indicates that the operation has violated a database integrity constraint.
 * When Spring is configured with Hibernate, the exception is thrown in the exception translation layer provided by Spring –
 * SessionFactoryUtils::convertHibernateAccessException.
 * <p>
 * There are three possible Hibernate exceptions that may cause the DataIntegrityViolationException to be thrown:
 * - org.hibernate.exception.ConstraintViolationException
 * - org.hibernate.PropertyValueException
 * - org.hibernate.exception.DataException
 * <p>
 * Source - https://www.baeldung.com/spring-dataIntegrityviolationexception
 */

@RestControllerAdvice
public class ConstraintViolationExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintViolationExceptionAdvice.class);

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ApiExceptionResponse> handle(ConstraintViolationException exception) {
        LOGGER.error("Bean validation exception has occurred", exception);

        var detailsList = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolationExceptionAdvice::mapConstraintToApiExceptionDetails)
                .collect(Collectors.toList());

        return ApiExceptionResponse
                .ofBadRequest("One of the input fields contains invalid value", detailsList)
                .asResponseEntity();
    }

    private static ApiExceptionDetails mapConstraintToApiExceptionDetails(ConstraintViolation<?> cv) {
        return new ApiExceptionDetails(null, cv.getMessage(), formatPath(cv.getPropertyPath()));
    }

    private static String formatPath(Path path) {
        var formattedPath = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, path.toString());

        return formattedPath.split("\\.").length < 3
                ? formattedPath
                : removeUntilDot(removeUntilDot(formattedPath));
    }

    private static String removeUntilDot(String message) {
        return message.substring(message.indexOf('.') + 1);
    }
}