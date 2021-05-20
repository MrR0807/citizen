package com.good.citizen.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.good.citizen.employees.model.Employee;
import io.pactfoundation.consumer.dsl.LambdaDsl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeesConsumerPact {

    @Value("${application.endpoints.employees}")
    private String employeeUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    @Pact(provider = "EmployeeApplication", consumer = "EmployeeConsumer")
    public RequestResponsePact getEmployeesPact(PactDslWithProvider builder) {
        return builder
                .uponReceiving("Get employees")
                .path(PathExtractor.extract(this.employeeUrl))
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(employeesBody())
                .toPact();
    }

    private static DslPart employeesBody() {
        return LambdaDsl.newJsonArrayMinLike(2, employees -> employees
                .object(employee -> employee
                        .id("id")
                        .numberType("socialSecurityNumber", 123456)
                        .stringType("firstName", "First")
                        .stringType("lastName", "Lastname")
                        .stringMatcher("jobTitle", "SOFTWARE_DEVELOPER|BUSINESS_ANALYST|PRODUCT_OWNER", "PRODUCT_OWNER")
                        .object("team", team -> team
                                .id()
                                .stringType("name", "Red")
                        )
                        .minArrayLike("projects", 0, projects -> {
                        })
                )
        ).build();
    }

    @Test
    @PactTestFor(pactMethod = "getEmployeesPact")
    void getAllEmployees(MockServer mockServer) {
        var url = UriComponentsBuilder.fromHttpUrl("http://localhost")
                .port(mockServer.getPort())
                .path(this.employeeUrl)
                .toUriString();

        var response = this.restTemplate.getForEntity(url, Employee[].class);
        var employee = response.getBody()[0];
        System.out.println(employee);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(employee).hasNoNullFieldsOrProperties();
    }

    @Pact(provider = "EmployeeApplication", consumer = "EmployeeConsumer")
    public RequestResponsePact getEmployeePact(PactDslWithProvider builder) {
        return builder.given("Get employee state")
                .uponReceiving("Get employee")
                .pathFromProviderState(PathExtractor.extract(this.employeeUrl) + "/${employeeId}", "/api/v1/employees/1234") //1234 has to match getEmployee method's path as well
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(this.oneEmployeeDsl())
                .toPact();
    }

    private DslPart oneEmployeeDsl() {
        return LambdaDsl.newJsonBody(employee -> employee
                .valueFromProviderState("id", "${employeeId}", 1234)
                .numberType("socialSecurityNumber", 123456789)
                .stringValue("firstName", "First")
                .stringValue("lastName", "Lastname")
                .stringMatcher("jobTitle", "SOFTWARE_DEVELOPER|BUSINESS_ANALYST|PRODUCT_OWNER")
                .object("team", team -> team
                        .id()
                        .stringValue("name", "Blue"))
                .array("projects", projects ->
                        projects.object(project -> project
                                .id()
                                .stringValue("name", "One")
                                .numberType("budget", 1000.00)
                        )
                )
        ).build();
    }

    @Test
    @PactTestFor(pactMethod = "getEmployeePact")
    void getEmployee(MockServer mockServer) {
        var url = UriComponentsBuilder.fromHttpUrl("http://localhost")
                .port(mockServer.getPort())
                .path(this.employeeUrl)
                .path("/1234") //This has to be equal to ``getEmployeePact`` pathFromProviderState ``example`` parameter
                .toUriString();

        var response = this.restTemplate.getForEntity(url, Employee.class);

        System.out.println(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }
}