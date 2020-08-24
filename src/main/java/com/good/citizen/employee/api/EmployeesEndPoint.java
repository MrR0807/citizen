package com.good.citizen.employee.api;

import com.good.citizen.employee.api.request.EmployeeRequest;
import com.good.citizen.employee.api.request.OldEmployeeRequest;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "${application.endpoints.employees}", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmployeesEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeesEndPoint.class);

    @GetMapping
    @ApiOperation("Get information about all employees")
    public void getAllEmployees() {
        LOGGER.info("Get all employees request");

    }

    @GetMapping("{id}")
    @ApiOperation("Get information about one employees")
    public void getEmployee(@PathVariable("id") @Min(1) Long id) {
        LOGGER.info("Get all employee request. Employee id: {}", id);
    }

    @PostMapping
    @ApiOperation("Add employee")
    public void addEmployee(@RequestBody @Valid EmployeeRequest request) {
        LOGGER.info("Add employee. Employee: {}", request);
    }
}