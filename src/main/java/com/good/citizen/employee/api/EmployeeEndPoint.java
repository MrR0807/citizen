package com.good.citizen.employee.api;

import com.good.citizen.employee.api.request.EmployeeRequest;
import com.good.citizen.employee.api.request.OldEmployeeRequest;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("${application.endpoints.first}")
public class EmployeeEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeEndPoint.class);

    public void getAllEmployees() {

    }

    public void getEmployee() {

    }

    public void addEmployee() {

    }

    public void getAllProjects() {

    }

    public void getProject() {

    }

    public void addProject() {

    }

    public void updateProject() {

    }

    public void closeProject() {

    }

    public void getAllTeams() {
    }

    public void getTeam() {

    }

    public void addTeam() {

    }

    public void updateTeam() {

    }

    public void generateSoldCarsStatistics() {

    }

    public void sellCar() {

    }

    @PostMapping("employee")
    @ApiOperation("This endpoint does something")
    public String getHello(@RequestBody OldEmployeeRequest request) {
        LOGGER.info("Got request: {}", request);

        return "hello";
    }

    @PostMapping("employee-record")
    @ApiOperation("This endpoint does something")
    public String getHello(@RequestBody EmployeeRequest request) {
        LOGGER.info("Got request: {}", request);

        return "hello";
    }
}