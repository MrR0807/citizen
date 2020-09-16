package com.good.citizen.employees.service;

import com.good.citizen.employees.api.request.EmployeeFilter;
import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.repo.EmployeeRepo;
import com.good.citizen.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepo repo;

    public EmployeeService(EmployeeRepo repo) {
        this.repo = repo;
    }

    public Set<Employee> getAllEmployees(EmployeeFilter filter) {
        return this.repo.getAllEmployees(filter).stream()
                .map(Employee::partialFrom)
                .collect(Collectors.toSet());
    }

    public Employee getEmployee(Long id) {
        return this.repo.getEmployee(id)
                .map(Employee::from)
                .orElseThrow(() -> new NotFoundException(String.format("Employee with id: %d does not exist", id)));
    }

    public void addEmployee() {

    }
}