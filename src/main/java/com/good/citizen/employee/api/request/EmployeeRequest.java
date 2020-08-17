package com.good.citizen.employee.api.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.employee.shared.JobTitle;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EmployeeRequest(
        String name,
        JobTitle jobTitle,
        Integer age,
        String previousTitle) {
}