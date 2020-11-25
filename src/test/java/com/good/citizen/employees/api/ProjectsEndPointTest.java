package com.good.citizen.employees.api;

import com.good.citizen.utils.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

@IntegrationTest
class ProjectsEndPointTest {

    @Value("${application.endpoints.projects}")
    private String url;

//    For demo purpose
//    @MockBean
//    private ProjectsService projectsService;

    @Test
    void getAllProjects() {
    }

    @Test
    void getProject() {
    }

    @Test
    void addProject() {
    }

    @Test
    void updateProject() {
    }

    @Test
    void closeProject() {
    }
}