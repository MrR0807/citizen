package com.good.citizen.employees.repo.entity;

import com.good.citizen.employees.shared.JobTitle;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "employee", schema = "employees")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long socialSecurityNumber;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private JobTitle jobTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    private TeamEntity team;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(schema = "employees", name = "employee_project",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"))
    private Set<ProjectEntity> projects = new HashSet<>();

    public EmployeeEntity() {
    }

    public void joinProject(ProjectEntity project) {
        project.getEmployees().add(this);
        this.projects.add(project);
    }

    public void leaveProject(ProjectEntity project) {
        project.getEmployees().remove(this);
        this.projects.remove(project);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSocialSecurityNumber() {
        return this.socialSecurityNumber;
    }

    public void setSocialSecurityNumber(Long socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public JobTitle getJobTitle() {
        return this.jobTitle;
    }

    public void setJobTitle(JobTitle jobTitle) {
        this.jobTitle = jobTitle;
    }

    public TeamEntity getTeam() {
        return this.team;
    }

    public void setTeam(TeamEntity teamEntity) {
        this.team = teamEntity;
    }

    public Set<ProjectEntity> getProjects() {
        return this.projects;
    }

    public void setProjects(Set<ProjectEntity> projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EmployeeEntity employeeEntity = (EmployeeEntity) o;
        return this.id != null && Objects.equals(this.id, employeeEntity.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}