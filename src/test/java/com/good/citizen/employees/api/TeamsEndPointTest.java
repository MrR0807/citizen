package com.good.citizen.employees.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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