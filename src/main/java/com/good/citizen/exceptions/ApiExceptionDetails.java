package com.good.citizen.exceptions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record ApiExceptionDetails(
        String code,
        String message,
        String fieldName) {

    @JsonCreator //Partial hack. In future Jackson should support records out of the box
    public ApiExceptionDetails {
    }
}