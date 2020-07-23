package com.good.citizen.api.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.shared.JobTitle;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EmployeeRecordRequest(
        String name,
        JobTitle jobTitle,
        Integer age,
        String previousTitle) {
}