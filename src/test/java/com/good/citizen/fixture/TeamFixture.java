package com.good.citizen.fixture;

import com.good.citizen.employees.model.Team;

public class TeamFixture {

    private TeamFixture() {
    }

    public static Team firstTeam() {
        return new Team(1L, "Team Blue");
    }

    public static Team secondTeam() {
        return new Team(2L, "Team Red");
    }

    public static Team thirdTeam() {
        return new Team(3L, "Team Green");
    }
}