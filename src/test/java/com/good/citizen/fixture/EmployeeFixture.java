package com.good.citizen.fixture;

import com.good.citizen.employees.model.Employee;

import java.util.List;
import java.util.Set;

import static com.good.citizen.employees.shared.JobTitle.BUSINESS_ANALYST;
import static com.good.citizen.employees.shared.JobTitle.PRODUCT_OWNER;
import static com.good.citizen.employees.shared.JobTitle.SOFTWARE_DEVELOPER;
import static com.good.citizen.fixture.TeamFixture.firstTeam;
import static com.good.citizen.fixture.TeamFixture.secondTeam;

public class EmployeeFixture {

    private EmployeeFixture() {
    }

    public static List<Employee> expectedEmployees() {
        return List.of(
                new Employee(1L, "First", "Lastname", SOFTWARE_DEVELOPER, firstTeam(), Set.of()),
                new Employee(2L, "Second", "Lastname", SOFTWARE_DEVELOPER, secondTeam(), Set.of()),
                new Employee(3L, "Third", "Lastname", BUSINESS_ANALYST, firstTeam(), Set.of()),
                new Employee(4L, "Fourth", "Lastname", PRODUCT_OWNER, firstTeam(), Set.of()));
    }

    public static Employee expectedEmployee() {
        return new Employee(1L, "First", "Lastname", SOFTWARE_DEVELOPER, firstTeam(), Set.of());
    }
}