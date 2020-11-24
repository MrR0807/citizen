package com.good.citizen.employees.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.employees.repo.entity.TeamEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Team(
        @Min(0) Long id, //When Team is being persisted, it won't have an id
        @NotBlank String name) {

    public static Team from(TeamEntity entity) {
        return new Team(entity.getId(), entity.getName());
    }
}