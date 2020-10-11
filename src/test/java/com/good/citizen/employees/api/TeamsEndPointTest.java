package com.good.citizen.employees.api;

import com.good.citizen.utils.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

@IntegrationTest
class TeamsEndPointTest {

    @Value("${application.endpoints.teams}")
    private String url;

    @Test
    void getAllTeams() {
    }

    @Test
    void getTeam() {
    }

    @Test
    void addTeam() {
    }

    @Test
    void updateTeam() {
    }
}