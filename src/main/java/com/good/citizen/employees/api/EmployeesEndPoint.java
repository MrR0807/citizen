package com.good.citizen.employees.api;

import com.good.citizen.employees.api.request.EmployeeFilter;
import com.good.citizen.employees.api.request.EmployeeRequest;
import com.good.citizen.employees.api.request.PatchEmployeeRequest;
import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.service.EmployeeService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Set;

@RestController
@RequestMapping(path = "${application.endpoints.employees}", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class EmployeesEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeesEndPoint.class);

    private final EmployeeService employeeService;

    public EmployeesEndPoint(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @ApiOperation("Get information about all employees")
    public Set<Employee> getAllEmployees(EmployeeFilter filter) {
        LOGGER.info("Get all employees request. Employee filter {}", filter);

        return this.employeeService.getAllEmployees(filter);
    }

    @GetMapping("{id}")
    @ApiOperation("Get information about one employees")
    public Employee getEmployee(@PathVariable("id") @Min(1) Long id) {
        LOGGER.info("Get employee request. Employee id: {}", id);

        return this.employeeService.getEmployee(id);
    }

    @PostMapping
    @ApiOperation("Add employee")
    public Employee addEmployee(@RequestBody @Valid EmployeeRequest request) {
        LOGGER.info("Add employee. Request: {}", request);

        return this.employeeService.addEmployee(request.toEmployee());
    }

    @PostMapping("{id}")
    @ApiOperation("Update employee")
    public Employee updateEmployee(@PathVariable("id") @Min(0) Long id, @RequestBody @Valid EmployeeRequest request) {
        LOGGER.info("Update employee. Employee id: {}. Request: {}", id, request);

        return this.employeeService.updateEmployee(id, request.toEmployee());
    }

    @PutMapping
    @ApiOperation("Add employee or update")
    public Employee putEmployee(@RequestBody @Valid EmployeeRequest request) {
        LOGGER.info("Add or update employee. Request: {}", request);

        return this.employeeService.putEmployee(request.toEmployee());
    }

    @PatchMapping("{id}")
    @ApiOperation("Patch employee")
    public Employee patchEmployee(@PathVariable("id") @Min(0) Long id, @RequestBody @Valid PatchEmployeeRequest request) {
        LOGGER.info("Patch employee. Request: {}", request);

        return this.employeeService.patchEmployee(id, request);
    }
}