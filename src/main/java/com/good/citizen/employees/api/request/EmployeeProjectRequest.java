package com.good.citizen.employees.api.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EmployeeProjectRequest(
        @NotNull @Min(0) Long employeeId,
        @NotNull @Min(0) Long projectId) {
}