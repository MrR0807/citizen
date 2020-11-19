package com.good.citizen.employees.service;

import com.good.citizen.employees.api.request.EmployeeFilter;
import com.good.citizen.employees.api.request.EmployeeRequest;
import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.repo.EmployeeRepo;
import com.good.citizen.employees.repo.TeamRepo;
import com.good.citizen.employees.repo.entity.EmployeeEntity;
import com.good.citizen.employees.repo.entity.TeamEntity;
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
        return this.repo.getEmployee(id)
                .map(Employee::from)
                .orElseThrow(() -> new NotFoundException("Employee with id: %d does not exist".formatted(id)));
    }

    @Transactional
    public void addEmployee(EmployeeRequest request) {
        var teamEntity = this.teamRepo.getTeam(request.team())
                .orElseThrow(() -> new NotFoundException("Team with name: %s does not exists".formatted(request.team())));
        var employeeEntity = mapToEntity(request, teamEntity);

        this.repo.save(employeeEntity);
    }

    private static EmployeeEntity mapToEntity(EmployeeRequest request, TeamEntity teamEntity) {
        var employeeEntity = new EmployeeEntity();
        employeeEntity.setSocialSecurityNumber(request.socialSecurityNumber());
        employeeEntity.setFirstName(request.name());
        employeeEntity.setLastName(request.lastName());
        employeeEntity.setJobTitle(request.jobTitle());
        employeeEntity.setTeam(teamEntity);
        return employeeEntity;
    }

    @Transactional
    public void putEmployee(EmployeeRequest request) {

    }
}