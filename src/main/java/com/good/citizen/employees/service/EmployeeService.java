package com.good.citizen.employees.service;

import com.good.citizen.employees.api.request.EmployeeFilter;
import com.good.citizen.employees.api.request.PatchEmployeeRequest;
import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.model.Team;
import com.good.citizen.employees.repo.EmployeeRepo;
import com.good.citizen.employees.repo.TeamRepo;
import com.good.citizen.employees.repo.entity.EmployeeEntity;
import com.good.citizen.employees.repo.entity.TeamEntity;
import com.good.citizen.employees.service.validator.EmployeeValidator;
import com.good.citizen.exceptions.BadRequestException;
import com.good.citizen.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepo repo;
    private final TeamRepo teamRepo;
    private final EmployeeValidator validator;

    public EmployeeService(EmployeeRepo repo, TeamRepo teamRepo, EmployeeValidator validator) {
        this.repo = repo;
        this.teamRepo = teamRepo;
        this.validator = validator;
    }

    public Set<Employee> getAllEmployees(EmployeeFilter filter) {
        return this.repo.getAllEmployees(filter).stream()
                .map(Employee::partialFrom)
                .collect(Collectors.toSet());
    }

    public Employee getEmployee(Long id) {
        return Employee.from(this.getEmployeeEntity(id));
    }

    private EmployeeEntity getEmployeeEntity(Long id) {
        return this.repo.getEmployee(id)
                .orElseThrow(() -> new NotFoundException("Employee with id: %d does not exist".formatted(id)));
    }

    private TeamEntity getTeamEntity(String teamName) {
        return this.teamRepo.getTeam(teamName)
                .orElseThrow(() -> new NotFoundException("Team with name: %s does not exists".formatted(teamName)));
    }

    private Team getTeam(String teamName) {
        return Team.from(this.getTeamEntity(teamName));
    }

    @Transactional
    public Employee addEmployee(Employee employee) {
        this.validator.validate(employee);
        if (this.repo.getEmployeeBySocialSecurityNumber(employee.socialSecurityNumber()).isPresent()) {
            throw new BadRequestException("Employee already exists");
        }
        return this.saveEmployee(employee);
    }

    private Employee saveEmployee(Employee employee) {
        var teamEntity = this.getTeamEntity(employee.team().name());
        var employeeEntity = EmployeeEntity.fromWithEmptyProjects(employee, teamEntity);
        this.repo.save(employeeEntity);

        return Employee.from(employeeEntity);
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee employee) {
        this.validator.validate(employee);
        return this.repo.getEmployee(id)
                .map(employeeEntity -> this.updateEmployee(employee, employeeEntity))
                .orElseThrow(() -> new NotFoundException("Employee with id: %d does not exist".formatted(id)));
    }

    private Employee updateEmployee(Employee employee, EmployeeEntity employeeEntity) {
        employeeEntity.setSocialSecurityNumber(employee.socialSecurityNumber());
        employeeEntity.setFirstName(employee.firstName());
        employeeEntity.setLastName(employee.lastName());
        employeeEntity.setJobTitle(employee.jobTitle());
        this.setTeam(employee, employeeEntity);

        return Employee.from(employeeEntity);
    }

    private void setTeam(Employee employee, EmployeeEntity employeeEntity) {
        var doesNotContainSameTeam = !employeeEntity.getTeam().getName().equalsIgnoreCase(employee.team().name());
        if (doesNotContainSameTeam) {
            var teamEntity = this.getTeamEntity(employee.team().name());
            employeeEntity.setTeam(teamEntity);
        }
    }

    @Transactional
    public Employee putEmployee(Employee employee) {
        this.validator.validate(employee);
        return this.repo.getEmployeeBySocialSecurityNumber(employee.socialSecurityNumber())
                .map(employeeEntity -> this.updateEmployee(employee, employeeEntity))
                .orElseGet(() -> this.saveEmployee(employee));
    }

    @Transactional
    public Employee patchEmployee(Long id, PatchEmployeeRequest request) {
        var employee = this.getEmployee(id);
        var patchedEmployee = this.patchEmployee(request, employee);

        return this.updateEmployee(id, patchedEmployee);
    }

    private Employee patchEmployee(PatchEmployeeRequest request, Employee employee) {
        var employeeBuilder = employee.builder();
        request.getSocialSecurityNumber().ifSet(employeeBuilder::setSocialSecurityNumber);
        request.getFirstName().ifSet(employeeBuilder::setFirstName);
        request.getLastName().ifSet(employeeBuilder::setLastName);
        request.getJobTitle().ifSet(employeeBuilder::setJobTitle);
        request.getTeam().ifSet(teamName ->
        {
            var team = this.getTeam(teamName);
            employeeBuilder.setTeam(team);
        });
        return employeeBuilder.build();
    }
}