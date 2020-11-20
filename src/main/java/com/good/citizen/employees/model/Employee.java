package com.good.citizen.employees.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.employees.repo.entity.EmployeeEntity;
import com.good.citizen.employees.shared.JobTitle;

import java.util.Set;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Employee(
        Long id,
        Long socialSecurityNumber,
        String firstName,
        String lastName,
        JobTitle jobTitle,
        Team team,
        Set<Project> projects) {

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