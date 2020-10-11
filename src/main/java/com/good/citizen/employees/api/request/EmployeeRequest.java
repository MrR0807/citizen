package com.good.citizen.employees.api.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.employees.shared.JobTitle;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EmployeeRequest(
        @NotBlank @Max(255) String name,
        @NotBlank @Max(255) String lastName,
        @NotBlank @Max(255) String team,
        @NotNull JobTitle jobTitle) {
}