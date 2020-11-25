package com.good.citizen.shared.validator;

import com.good.citizen.shared.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JavaxValidator {

    private final Validator validator;

    @Autowired
    public JavaxValidator(@Qualifier("customLocalValidatorFactoryBean") Validator javaxValidator) {
        this.validator = javaxValidator;
    }

    public <T> List<Result.Error> validate(T t) {
        var violations = this.validator.validate(t);

        if (violations.isEmpty()) {
            return List.of();
        }

        return violations.stream()
                .map(violation -> new Result.Error(violation.getMessage(), violation.getPropertyPath().toString()))
                .collect(Collectors.toList());
    }
}