package com.good.citizen.exceptions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.good.citizen.shared.JsonFormatPattern;
import com.good.citizen.shared.TimeMachine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record ApiExceptionResponse(
        int statusCode,
        @JsonFormat(pattern = JsonFormatPattern.TIMESTAMP_FORMAT)
        LocalDateTime timestamp,
        String reason,
        List<ApiExceptionDetails> exceptions) {

        public static ApiExceptionResponse ofBadRequest(String reason, List<ApiExceptionDetails> exceptions) {
                return new ApiExceptionResponse(HttpStatus.BAD_REQUEST.value(), TimeMachine.nowLocalDateAndTime(), reason, exceptions);
        }

        public static ApiExceptionResponse ofBadRequest(String reason) {
                return ofBadRequest(reason, List.of());
        }

        public static ApiExceptionResponse ofInternalServerError(String reason) {
                return new ApiExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), TimeMachine.nowLocalDateAndTime(), reason, List.of());
        }

        public static ApiExceptionResponse ofRequestTimeout(String reason) {
                return new ApiExceptionResponse(HttpStatus.REQUEST_TIMEOUT.value(), TimeMachine.nowLocalDateAndTime(), reason, List.of());
        }

        public static ApiExceptionResponse ofNotFound(String reason) {
                return new ApiExceptionResponse(HttpStatus.NOT_FOUND.value(), TimeMachine.nowLocalDateAndTime(), reason, List.of());
        }

        public ResponseEntity<ApiExceptionResponse> asResponseEntity() {
                return new ResponseEntity<>(this, HttpStatus.valueOf(this.statusCode()));
        }
}