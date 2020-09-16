package com.good.citizen.employees.api;

import com.good.citizen.employees.api.request.EmployeeRequest;
import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.model.Team;
import com.good.citizen.employees.shared.JobTitle;
import com.good.citizen.fixture.EmployeeFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeesEndPointTest {

    @Value("${application.endpoints.employees}")
    private String url;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllEmployees() {
        var response = this.restTemplate.getForEntity(this.url, Employee[].class);
        var actualEmployees = List.of(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualEmployees).containsExactlyInAnyOrderElementsOf(EmployeeFixture.expectedEmployees());
    }

    @Test
    void getAllEmployees__givenTeamsFilter() {
        var url = UriComponentsBuilder.fromUriString(this.url)
                .queryParam("team", "    team   bLUE    ") //weird type on purpose
                .build().toString();

        var response = this.restTemplate.getForEntity(url, Employee[].class);
        var actualEmployees = List.of(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualEmployees).extracting(Employee::team).containsOnly(new Team(1L, "Team Blue"));
    }

    @Test
    void getAllEmployees__givenTeamFilterWhichDoesNotExists__thenReturnEmptyList() {
        var url = UriComponentsBuilder.fromUriString(this.url)
                .queryParam("team", "Does not exist")
                .build().toString();

        var response = this.restTemplate.getForEntity(url, Employee[].class);
        var actualEmployees = List.of(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualEmployees).isEmpty();
    }

    @Test
    void getAllEmployees__givenJobTitleFilter() {
        var url = UriComponentsBuilder.fromUriString(this.url)
                .queryParam("jobTitle", JobTitle.SOFTWARE_DEVELOPER)
                .build().toString();

        var response = this.restTemplate.getForEntity(url, Employee[].class);
        var actualEmployees = List.of(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualEmployees).extracting(Employee::jobTitle).containsOnly(JobTitle.SOFTWARE_DEVELOPER);
    }

    @Test
    void getEmployee__thenReturnOneEmployee() {
        var response = this.restTemplate.getForEntity(this.url + "/1", Employee.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(EmployeeFixture.expectedEmployee());
    }

    @Test
    void getEmployee__whenInvalidEmployeeId__thenReturn400() {
        var response = this.restTemplate.getForEntity(this.url + "/-1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Bad Request");
    }

    @Test
    void addEmployee() {
        var request = new EmployeeRequest("Hello", "Lastname", "Team Green", JobTitle.PRODUCT_OWNER);
        var response = this.restTemplate.postForEntity(this.url, request, Employee.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void addEmployee__whenTeamDoesNotExist__thenReturn404() {
        var request = new EmployeeRequest("Hello", "Lastname", "Team Does Not Exists", JobTitle.PRODUCT_OWNER);
        var response = this.restTemplate.postForEntity(this.url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Team does not exist");
    }

    @Test
    void addEmployee__whenRequestInvalid__thenReturn400() {
        var request = new EmployeeRequest(null, "Lastname", "Team Does Not Exists", JobTitle.PRODUCT_OWNER);
        var response = this.restTemplate.postForEntity(this.url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .contains("Invalid name")
                .contains("Invalid age");
    }

    @Test
    void getAllProjects() {
    }
}