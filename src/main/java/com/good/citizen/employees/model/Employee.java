package com.good.citizen.employees.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.employees.repo.entity.EmployeeEntity;
import com.good.citizen.employees.shared.JobTitle;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Employee(
        @NotNull @Min(0) Long id,
        @NotNull @Min(0) Long socialSecurityNumber,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull JobTitle jobTitle,
        @NotNull @Valid Team team,
        @NotNull Set<@NotNull @Valid Project> projects) {

    public static Employee partialFrom(EmployeeEntity entity) {
        return new Employee(entity.getId(), entity.getSocialSecurityNumber(), entity.getFirstName(), entity.getLastName(),
                entity.getJobTitle(), Team.from(entity.getTeam()), Set.of());
    }

    public static Employee from(EmployeeEntity entity) {
        var projects = entity.getProjects().stream()
                .map(Project::from)
                .collect(Collectors.toSet());

        return new Employee(entity.getId(), entity.getSocialSecurityNumber(), entity.getFirstName(), entity.getLastName(), entity.getJobTitle(),
                Team.from(entity.getTeam()), projects);
    }
}