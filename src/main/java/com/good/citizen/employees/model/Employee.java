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
        @Min(0) Long id, //When Employee is created, it won't have an id
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

    public EmployeeBuilder builder() {
        return new EmployeeBuilder(this);
    }

    public static class EmployeeBuilder {
        private final Long id;
        private Long socialSecurityNumber;
        private String firstName;
        private String lastName;
        private JobTitle jobTitle;
        private Team team;
        private Set<Project> projects;

        public EmployeeBuilder(Employee employee) {
            this.id = employee.id;
            this.socialSecurityNumber = employee.socialSecurityNumber;
            this.firstName = employee.firstName;
            this.lastName = employee.lastName;
            this.jobTitle = employee.jobTitle;
            this.team = employee.team;
            this.projects = employee.projects;
        }

        public EmployeeBuilder setSocialSecurityNumber(Long socialSecurityNumber) {
            this.socialSecurityNumber = socialSecurityNumber;
            return this;
        }

        public EmployeeBuilder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public EmployeeBuilder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public EmployeeBuilder setJobTitle(JobTitle jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }

        public EmployeeBuilder setTeam(Team team) {
            this.team = team;
            return this;
        }

        public EmployeeBuilder setProjects(Set<Project> projects) {
            this.projects = projects;
            return this;
        }

        public Employee build() {
            return new Employee(this.id, this.socialSecurityNumber, this.firstName, this.lastName, this.jobTitle, this.team, this.projects);
        }
    }
}