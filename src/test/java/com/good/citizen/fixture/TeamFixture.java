package com.good.citizen.fixture;

import com.good.citizen.employees.model.Team;

public class TeamFixture {

    private TeamFixture() {
    }

    public static Team firstTeam() {
        return new Team(1L, "Blue");
    }

    public static Team secondTeam() {
        return new Team(2L, "Red");
    }

    public static Team thirdTeam() {
        return new Team(3L, "Green");
    }
}