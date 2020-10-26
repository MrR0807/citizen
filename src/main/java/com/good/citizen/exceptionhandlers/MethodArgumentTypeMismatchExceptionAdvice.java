package com.good.citizen.exceptionhandlers;

import com.good.citizen.exceptions.ApiExceptionDetails;
import com.good.citizen.exceptions.ApiExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * This is thrown when given request argument does not correspond to Java's type.
 * For example, request provides string, when we expect a number.
 * <p>
 * Also, note, that this Advice contains @Order annotation. Order is required, because it has to registered as high as possible in
 * ExceptionHandlerExceptionResolver linkedList. Otherwise, depending on registration order, it might delegate to IllegalArgumentExceptionAdvice.
 *
 * @see org.springframework.web.method.annotation.ExceptionHandlerMethodResolver
 * @see org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver initExceptionHandlerAdviceCache
 * @see org.springframework.core.annotation.Order
 * @see org.springframework.core.Ordered
 */

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MethodArgumentTypeMismatchExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodArgumentTypeMismatchExceptionAdvice.class);

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiExceptionResponse> handle(MethodArgumentTypeMismatchException exception) {
        LOGGER.error("MethodArgumentTypeMismatchException", exception);

        var apiException = convertToApiException(exception);
        return ApiExceptionResponse
                .ofBadRequest("One of the input fields contains invalid value", List.of(apiException))
                .asResponseEntity();
    }

    private static ApiExceptionDetails convertToApiException(MethodArgumentTypeMismatchException exception) {
        // It is advised to throw IllegalArgumentException in Spring docs, for custom Converters.
        // In a case, when this type of exception is the cause, try to extract exception message and return it to client.
        // When it's not possible to do, at least return the field on which conversion failed
        if (exception.getCause() instanceof ConversionFailedException cause) {
            var errorMessage = cause.getCause() instanceof IllegalArgumentException
                    ? cause.getCause().getMessage()
                    : String.format("Invalid value %s", cause.getValue());
            return new ApiExceptionDetails(null, errorMessage, exception.getName());
        }
        return new ApiExceptionDetails(null, "", exception.getName());
    }
}