package com.good.citizen.employees.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.good.citizen.employees.repo.entity.ProjectEntity;

import java.math.BigDecimal;
import java.util.Optional;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Project(
        Long id,
        String name,
        Optional<BigDecimal> budget) {

    @JsonCreator
    public Project(Long id, String name, BigDecimal budget) {
        this(id, name, Optional.ofNullable(budget));
    }

    public static Project from(ProjectEntity entity) {
        return new Project(entity.getId(), entity.getName(), entity.getBudget());
    }
}
