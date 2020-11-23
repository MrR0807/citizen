package com.good.citizen.employees.service;

import com.good.citizen.employees.api.request.EmployeeFilter;
import com.good.citizen.employees.api.request.EmployeeRequest;
import com.good.citizen.employees.api.request.PatchEmployeeRequest;
import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.repo.EmployeeRepo;
import com.good.citizen.employees.repo.TeamRepo;
import com.good.citizen.employees.repo.entity.EmployeeEntity;
import com.good.citizen.employees.repo.entity.TeamEntity;
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

    public EmployeeService(EmployeeRepo repo, TeamRepo teamRepo) {
        this.repo = repo;
        this.teamRepo = teamRepo;
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

    @Transactional
    public Employee addEmployee(EmployeeRequest request) {
        if (this.repo.getEmployeeBySocialSecurityNumber(request.socialSecurityNumber()).isPresent()) {
            throw new BadRequestException("Employee already exists");
        }
        return this.saveEmployee(request);
    }

    private Employee saveEmployee(EmployeeRequest request) {
        var teamEntity = this.getTeamEntity(request.team());
        var employeeEntity = EmployeeEntity.fromWithEmptyProjects(request, teamEntity);
        this.repo.save(employeeEntity);

        return Employee.from(employeeEntity);
    }

    @Transactional
    public Employee updateEmployee(Long id, EmployeeRequest request) {
        return this.repo.getEmployee(id)
                .map(employeeEntity -> this.updateEmployee(request, employeeEntity))
                .orElseThrow(() -> new NotFoundException("Employee with id: %d does not exist".formatted(id)));
    }

    private Employee updateEmployee(EmployeeRequest request, EmployeeEntity employeeEntity) {
        employeeEntity.setSocialSecurityNumber(request.socialSecurityNumber());
        employeeEntity.setFirstName(request.firstName());
        employeeEntity.setLastName(request.lastName());
        employeeEntity.setJobTitle(request.jobTitle());
        this.setTeam(request, employeeEntity);

        return Employee.from(employeeEntity);
    }

    private void setTeam(EmployeeRequest request, EmployeeEntity employeeEntity) {
        var doesNotContainSameTeam = !employeeEntity.getTeam().getName().equalsIgnoreCase(request.team());
        if (doesNotContainSameTeam) {
            var teamEntity = this.getTeamEntity(request.team());
            employeeEntity.setTeam(teamEntity);
        }
    }

    @Transactional
    public Employee putEmployee(EmployeeRequest request) {
        return this.repo.getEmployeeBySocialSecurityNumber(request.socialSecurityNumber())
                .map(employeeEntity -> this.updateEmployee(request, employeeEntity))
                .orElseGet(() -> this.saveEmployee(request));
    }

    public Employee patchEmployee(Long id, PatchEmployeeRequest request) {

        return null;
    }
}