package com.good.citizen.employees.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.good.citizen.employees.repo.entity.TeamEntity;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Team(
        Long id,
        String name) {

    public static Team from(TeamEntity entity) {
        return new Team(entity.getId(), entity.getName());
    }
}