package com.good.citizen.employees.service;

import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.repo.EmployeeRepo;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepo repo;

    public EmployeeService(EmployeeRepo repo) {
        this.repo = repo;
    }

    public Set<Employee> getAllEmployees() {
        return repo.getAllEmployees().stream()
                .map(Employee::partialFrom)
                .collect(Collectors.toSet());
    }

    public Employee getEmployee() {
        return null;
    }

    public void addEmployee() {

    }
}