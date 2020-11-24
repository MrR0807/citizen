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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.good.citizen.employees.shared.JobTitle.BUSINESS_ANALYST;
import static com.good.citizen.employees.shared.JobTitle.PRODUCT_OWNER;
import static com.good.citizen.employees.shared.JobTitle.SOFTWARE_DEVELOPER;
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
                .queryParam("team", "    bLUE    ") //weird type on purpose
                .build().toString();

        var response = this.restTemplate.getForEntity(url, Employee[].class);
        var actualEmployees = List.of(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualEmployees).extracting(Employee::team).containsOnly(new Team(1L, "Blue"));
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
    void getEmployee__whenEmployeeDoesNotExists__thenReturn404() {
        var response = this.restTemplate.getForEntity(this.url + "/999", ApiExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().reason()).isEqualTo("Employee with id: 999 does not exist");
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
        var request = new EmployeeRequest(1L, "Hello", "Lastname", "Green", JobTitle.PRODUCT_OWNER);
        var response = this.restTemplate.postForEntity(this.url, request, Employee.class);
        var employee = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        //assertThat(employee).isEqualToComparingFieldByField(request); does not work due to how records are implemented
        assertThat(employee.id()).isEqualTo(5L);
        assertThat(employee.socialSecurityNumber()).isEqualTo(request.socialSecurityNumber());
        assertThat(employee.firstName()).isEqualTo(request.firstName());
        assertThat(employee.lastName()).isEqualTo(request.lastName());
        assertThat(employee.team().name()).isEqualTo(request.team());
        assertThat(employee.jobTitle()).isEqualTo(request.jobTitle());
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
    void addEmployee__whenEmployeeExists__thenReturn400() {
        var request = new EmployeeRequest(123456789L, "First", "Lastname", "Green", SOFTWARE_DEVELOPER);
        var response = this.restTemplate.postForEntity(this.url, request, ApiExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().reason()).isEqualTo("Employee already exists");
    }

    /**
     * In scrip V1.0__add_employees.sql exists (1, 123456789, 'First', 'Lastname', 'SOFTWARE_DEVELOPER', 1). Last id is 1 which corresponds to Blue team
     */
    @Test
    void updateEmployee() {
        var request = new EmployeeRequest(777_777_777L, "FirstuUpdate", "LastUpdate", "Red", PRODUCT_OWNER);
        var response = this.restTemplate.postForEntity(this.url + "/1", request, Employee.class);
        var employee = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(employee.id()).isEqualTo(1L);
        assertThat(employee.socialSecurityNumber()).isEqualTo(777_777_777L);
        assertThat(employee.firstName()).isEqualTo("FirstuUpdate");
        assertThat(employee.lastName()).isEqualTo("LastUpdate");
        assertThat(employee.team().name()).isEqualTo("Red");
        assertThat(employee.jobTitle()).isEqualTo(PRODUCT_OWNER);
    }

    @Test
    void updateEmployee__whenEmployeeDoesNotExists__thenReturn404() {
        var request = new EmployeeRequest(777_777_777L, "FirstuUpdate", "LastUpdate", "Red", PRODUCT_OWNER);
        var response = this.restTemplate.postForEntity(this.url + "/999", request, ApiExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().reason()).isEqualTo("Employee with id: 999 does not exist");
    }

    @Test
    void updateEmployee__givenInvalidTeam__return404() {
        var request = new EmployeeRequest(777_777_777L, "FirstuUpdate", "LastUpdate", "Bad Team", PRODUCT_OWNER);
        var response = this.restTemplate.postForEntity(this.url + "/999", request, ApiExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().reason()).isEqualTo("Employee with id: 999 does not exist");
    }

    @Test
    void putEmployee__whenDoesNotExists__thenCreate() {
        var request = new EmployeeRequest(9L, "FirstuCreate", "LastCreate", "Red", PRODUCT_OWNER);
        var response = this.restTemplate.exchange(this.url, HttpMethod.PUT, new HttpEntity<>(request), Employee.class);
        var employee = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(employee.id()).isEqualTo(5L);
        assertThat(employee.socialSecurityNumber()).isEqualTo(9L);
        assertThat(employee.firstName()).isEqualTo("FirstuCreate");
        assertThat(employee.lastName()).isEqualTo("LastCreate");
        assertThat(employee.team().name()).isEqualTo("Red");
        assertThat(employee.jobTitle()).isEqualTo(PRODUCT_OWNER);
    }

    /**
     * In scrip V1.0__add_employees.sql exists (1, 123456789, 'First', 'Lastname', 'SOFTWARE_DEVELOPER', 1). Last id is 1 which corresponds to Blue team
     */
    @Test
    void putEmployee__whenExists__thenUpdate() {
        var request = new EmployeeRequest(123_456_789L, "FirstuUpdate", "LastUpdate", "Red", PRODUCT_OWNER);
        var response = this.restTemplate.exchange(this.url, HttpMethod.PUT, new HttpEntity<>(request), Employee.class);
        var employee = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(employee.id()).isEqualTo(1L);
        assertThat(employee.socialSecurityNumber()).isEqualTo(123_456_789L);
        assertThat(employee.firstName()).isEqualTo("FirstuUpdate");
        assertThat(employee.lastName()).isEqualTo("LastUpdate");
        assertThat(employee.team().name()).isEqualTo("Red");
        assertThat(employee.jobTitle()).isEqualTo(PRODUCT_OWNER);
    }

    @Test
    void putEmployee__whenInvalidRequest__then400() {
        var request = new EmployeeRequest(-1L, "FirstuUpdate", "LastUpdate", "Does Not exists", PRODUCT_OWNER);
        var response = this.restTemplate.exchange(this.url, HttpMethod.PUT, new HttpEntity<>(request), ApiExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().reason()).isEqualTo("One of the input fields contains invalid value");
    }

    /**
     * In scrip V1.0__add_employees.sql exists (1, 123456789, 'First', 'Lastname', 'SOFTWARE_DEVELOPER', 1). Last id is 1 which corresponds to Blue team
     */
    @Test
    void patchEmployee() {
        var response = this.restTemplate.exchange(this.url + "/1", HttpMethod.PATCH, buildPatchRequest(), Employee.class);
        var employee = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(employee.id()).isEqualTo(1L);
        assertThat(employee.socialSecurityNumber()).isEqualTo(78L);
        assertThat(employee.firstName()).isEqualTo("PatchName");
        assertThat(employee.lastName()).isEqualTo("PatchLast");
        assertThat(employee.team().name()).isEqualTo("Green");
        assertThat(employee.jobTitle()).isEqualTo(BUSINESS_ANALYST);
    }

    private static HttpEntity<String> buildPatchRequest() {
        var requestBody = """
                {
                  "firstName": "PatchName",
                  "jobTitle": "BUSINESS_ANALYST",
                  "lastName": "PatchLast",
                  "socialSecurityNumber": 78,
                  "team": "GREEN"
                }
                """;
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(requestBody, headers);
    }

    @Test
    void patchEmployee__whenEmployeeDoesNotExist__thenReturn404() {
        var request = buildPatchRequest();
        var response = this.restTemplate.exchange(this.url + "/999", HttpMethod.PATCH, new HttpEntity<>(request), ApiExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().reason()).isEqualTo("Employee with id: 999 does not exist");
    }

    @Test
    void patchEmployee__whenInvalidRequest__thenReturn400() {
        var request = buildPatchRequest();
//        request.setFirstName("   "); //Setting blank should trigger validator
        var response = this.restTemplate.exchange(this.url + "/1", HttpMethod.PATCH, new HttpEntity<>(request), ApiExceptionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().reason()).isEqualTo("One of the input fields contains invalid value");
        assertThat(response.getBody().exceptions()).hasSize(1);
        assertThat(response.getBody().exceptions()).element(0).extracting(ApiExceptionDetails::message).isEqualTo("");
    }
}