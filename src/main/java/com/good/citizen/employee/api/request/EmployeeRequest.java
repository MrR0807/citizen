package com.good.citizen.employee.api.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.employee.shared.JobTitle;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EmployeeRequest(
        @NotEmpty String name,
        @NotNull JobTitle jobTitle,
        @Min(18) Integer age) {
}