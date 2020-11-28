package com.good.citizen.employees.springway;

import com.good.citizen.employees.api.request.EmployeeFilter;
import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.model.Project;
import com.good.citizen.exceptions.NotFoundException;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.Set;
import java.util.stream.Collectors;

//For demo purposes only.
@RestController
@RequestMapping(value = "/spring/employee", produces = MediaType.APPLICATION_JSON_VALUE)
public class SpringEmployeesEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringEmployeesEndPoint.class);

    private final EmployeeRepoSpringData repo;
    private final ProjectRepoSpringData projectRepo;

    public SpringEmployeesEndPoint(EmployeeRepoSpringData repo, ProjectRepoSpringData projectRepo) {
        this.repo = repo;
        this.projectRepo = projectRepo;
    }

    @GetMapping
    @ApiOperation("Get information about all employees")
    public Set<Employee> getAllEmployees(EmployeeFilter filter) {
        LOGGER.info("Get all employees request. Employee filter {}", filter);

        var employeeSpecification = EmployeeSpecification.createEmployeeSpecification(filter);
        return this.repo.findAll(employeeSpecification).stream()
                .map(Employee::partialFrom)
                .collect(Collectors.toSet());
    }

    @GetMapping("{id}")
    @ApiOperation("Get information about one employees")
    public Employee getEmployee(@PathVariable("id") @Min(1) Long id) {
        LOGGER.info("Spring Endpoint. Get employee request. Employee id: {}", id);

        return this.repo.findById(id)
                .map(Employee::from)
                .orElseThrow(() -> new NotFoundException("Employee with id: %d does not exist".formatted(id)));
    }

    @GetMapping("project/{id}")
    @ApiOperation("Get information about one project")
    public Project getProject(@PathVariable("id") @Min(1) Long id) {
        LOGGER.info("Spring Endpoint. Get project request. Employee id: {}", id);

        return this.projectRepo.findById(id)
                .map(Project::from)
                .orElseThrow(() -> new NotFoundException("Employee with id: %d does not exist".formatted(id)));
    }
}