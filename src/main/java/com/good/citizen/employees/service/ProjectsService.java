package com.good.citizen.employees.service;

import com.good.citizen.employees.api.request.EmployeeProjectRequest;
import com.good.citizen.employees.repo.EmployeeRepo;
import com.good.citizen.employees.repo.ProjectRepo;
import com.good.citizen.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ProjectsService {

    private final ProjectRepo projectRepo;
    private final EmployeeRepo repo;

    public ProjectsService(ProjectRepo projectRepo, EmployeeRepo repo) {
        this.projectRepo = projectRepo;
        this.repo = repo;
    }

    @Transactional
    public void addEmployeeToProject(EmployeeProjectRequest request) {
        var employeeEntity = this.repo.getEmployee(request.employeeId())
                .orElseThrow(() -> new NotFoundException("Employee with id: %d does not exist".formatted(request.employeeId())));

        var projectEntity = this.projectRepo.getProject(request.projectId())
                .orElseThrow(() -> new NotFoundException("Project with id: %d does not exists".formatted(request.projectId())));

        employeeEntity.getProjects().add(projectEntity);
    }
}