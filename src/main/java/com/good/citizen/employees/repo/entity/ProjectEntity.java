package com.good.citizen.employees.repo.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "project", schema = "employees")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal budget;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "projects")
    private Set<EmployeeEntity> employees;

    public ProjectEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<BigDecimal> getBudget() {
        return Optional.ofNullable(budget);
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public Set<EmployeeEntity> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<EmployeeEntity> employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectEntity projectEntity = (ProjectEntity) o;
        return id != null && Objects.equals(id, projectEntity.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}