package com.good.citizen.employees.api;

import com.good.citizen.employees.api.request.EmployeeRequest;
import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.model.Team;
import com.good.citizen.employees.shared.JobTitle;
import com.good.citizen.exceptions.ApiExceptionDetails;
import com.good.citizen.exceptions.ApiExceptionResponse;
import com.good.citizen.fixture.EmployeeFixture;
import com.good.citizen.utils.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, //AFTER EACH because initial database setup is done by Flyway
        scripts = {
                "classpath:db/migration/h2/clear_employee.sql",
                "classpath:db/migration/h2/V1.0__add_employees.sql"})
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
        var response = this.restTemplate.getForEntity(this.url + "/-1", ApiExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().reason()).contains("One of the input fields contains invalid value");
        assertThat(response.getBody().exceptions()).hasSize(1);
        assertThat(response.getBody().exceptions()).element(0).extracting(ApiExceptionDetails::message)
                .isEqualTo("must be greater than or equal to 1");
    }

    @Test
    void addEmployee() {
        var request = new EmployeeRequest(1L, "Hello", "Lastname", "Team Green", JobTitle.PRODUCT_OWNER);
        var response = this.restTemplate.postForEntity(this.url, request, Employee.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void addEmployee__whenTeamDoesNotExist__thenReturn404() {
        var request = new EmployeeRequest(1L, "Hello", "Lastname", "Team Does Not Exists", JobTitle.PRODUCT_OWNER);
        var response = this.restTemplate.postForEntity(this.url, request, ApiExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().reason()).isEqualTo("Team with name: Team Does Not Exists does not exists");
    }

    @Test
    void addEmployee__whenRequestInvalid__thenReturn400() {
        var request = new EmployeeRequest(1L, null, "Lastname", "Team Does Not Exists", JobTitle.PRODUCT_OWNER);
        var response = this.restTemplate.postForEntity(this.url, request, ApiExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().reason()).isEqualTo("One of the input fields contains invalid value");
    }

    @Test
    void getAllProjects() {
    }
}