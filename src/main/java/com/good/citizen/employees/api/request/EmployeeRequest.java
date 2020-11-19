package com.good.citizen.employees.api.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.employees.shared.JobTitle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EmployeeRequest(
        @NotNull Long socialSecurityNumber,
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 255) String lastName,
        @NotBlank @Size(max = 255) String team,
        @NotNull JobTitle jobTitle) {
}