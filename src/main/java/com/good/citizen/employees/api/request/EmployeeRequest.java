package com.good.citizen.employees.api.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.employees.shared.JobTitle;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EmployeeRequest(
        @NotEmpty String name,
        @NotEmpty String lastName,
        @NotEmpty String team,
        @NotNull JobTitle jobTitle,
        @Min(18) @Max(75) Integer age) {
}