package com.good.citizen.employees.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeesEndPointTest {

    @Value("${application.endpoints.employees}")
    private String url;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void getAllEmployees() throws URISyntaxException {
        var request = RequestEntity.get(new URI(url)).build();

        var response = testRestTemplate.exchange(request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getEmployee() {
    }

    @Test
    void addEmployee() {
    }

    @Test
    void getAllProjects() {
    }
}