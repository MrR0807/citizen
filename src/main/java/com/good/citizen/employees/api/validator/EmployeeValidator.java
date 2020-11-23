package com.good.citizen.employees.api.validator;

import com.good.citizen.employees.model.Employee;
import com.good.citizen.exceptions.BadRequestException;
import com.good.citizen.shared.validator.JavaxValidator;
import com.good.citizen.shared.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public class EmployeeValidator extends Validator<Employee> {

    public EmployeeValidator(JavaxValidator javaxValidator) {
        super(javaxValidator);
    }

    @Override
    protected void validate(Employee employee) {
        var errors = this.javaxValidate(employee);

        if (errors.size() != 0) {
            throw new BadRequestException("Employee contains invalid parameters", errors);
        }
    }
}