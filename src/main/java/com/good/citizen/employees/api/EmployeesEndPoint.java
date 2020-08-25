package com.good.citizen.employees.api;

import com.good.citizen.employees.api.request.EmployeeRequest;
import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.service.EmployeeService;
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
import java.util.Set;

@RestController
@RequestMapping(path = "${application.endpoints.employees}",  produces = MediaType.APPLICATION_JSON_VALUE)
public class EmployeesEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeesEndPoint.class);

    private final EmployeeService employeeService;

    public EmployeesEndPoint(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @ApiOperation("Get information about all employees")
    public Set<Employee> getAllEmployees() {
        LOGGER.info("Get all employees request");

        return employeeService.getAllEmployees();
    }

    @GetMapping("{id}")
    @ApiOperation("Get information about one employees")
    public void getEmployee(@PathVariable("id") @Min(1) Long id) {
        LOGGER.info("Get all employee request. Employee id: {}", id);

        employeeService.getEmployee();
    }

    @PostMapping
    @ApiOperation("Add employee")
    public void addEmployee(@RequestBody @Valid EmployeeRequest request) {
        LOGGER.info("Add employee. Employee: {}", request);

        employeeService.addEmployee();
    }
}