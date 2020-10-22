package com.good.citizen.employees.service;

import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.repo.EmployeeRepoSpringData;
import com.good.citizen.exceptions.NotFoundException;

//For demo purposes only.
public class EmployeeServiceSpring {

    private final EmployeeRepoSpringData repo;

    public EmployeeServiceSpring(EmployeeRepoSpringData repo) {
        this.repo = repo;
    }

    public Employee getEmployee(Long id) {
        return this.repo.findById(id)
                .map(Employee::from)
                .orElseThrow(() -> new NotFoundException(String.format("Employee with id: %d does not exist", id)));
    }
}