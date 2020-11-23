package com.good.citizen.shared.validator;

import com.good.citizen.exceptions.ApiExceptionDetails;
import com.good.citizen.shared.Result;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Validator<T> {

    private final JavaxValidator javaxValidator;

    public Validator(JavaxValidator javaxValidator) {
        this.javaxValidator = javaxValidator;
    }

    public ApiExceptionDetails[] mapFromErrors(List<Result.Error> errors) {
        return errors.stream()
                .map(Result.Error::getDetails)
                .toArray(ApiExceptionDetails[]::new);
    }

    public List<Result.Error> validateViewGiven(List<? extends Validation<T>> validations, T t) {
        return validations.stream()
                .filter(validation -> validation.supports(t))
                .map(validation -> validation.validate(t))
                .filter(result -> result instanceof Result.Error)
                .map(result -> (Result.Error) result)
                .collect(Collectors.toList());
    }

    public List<Result.Error> javaxValidate(T t) {
        return this.javaxValidator.validate(t);
    }

    protected abstract void validate(T t);
}