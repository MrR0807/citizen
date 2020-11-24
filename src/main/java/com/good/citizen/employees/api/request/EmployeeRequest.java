package com.good.citizen.employees.api.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.model.Team;
import com.good.citizen.employees.shared.JobTitle;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EmployeeRequest(
        @NotNull @Min(0) Long socialSecurityNumber,
        @NotBlank @Size(max = 255) String firstName,
        @NotBlank @Size(max = 255) String lastName,
        @NotBlank @Size(max = 255) String team,
        @NotNull JobTitle jobTitle) {

    public Employee toEmployee() {
        return new Employee(null, this.socialSecurityNumber, this.firstName, this.lastName, this.jobTitle, new Team(null, this.team), Set.of());
    }
}