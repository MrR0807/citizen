package com.good.citizen.fixture;

import com.good.citizen.employees.model.Project;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class ProjectFixture {

    public static Set<Project> allProjects() {
        return Set.of(firstProject(), projectZ(), projectX());
    }

    public static Project firstProject() {
        return new Project(1L, "One", BigDecimal.valueOf(1_000.00).setScale(2, RoundingMode.HALF_UP));
    }

    public static Project projectZ() {
        return new Project(2L, "Z", BigDecimal.valueOf(1000_000));
    }

    public static Project projectX() {
        return new Project(3L, "X", (BigDecimal) null);
    }
}