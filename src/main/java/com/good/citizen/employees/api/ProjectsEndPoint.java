package com.good.citizen.employees.api;

import com.good.citizen.employees.api.request.EmployeeProjectRequest;
import com.good.citizen.employees.service.ProjectsService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "${application.endpoints.projects}", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class ProjectsEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectsEndPoint.class);

    private final ProjectsService projectsService;

    public ProjectsEndPoint(ProjectsService projectsService) {
        this.projectsService = projectsService;
    }

    public void getAllProjects() {

    }

    public void getProject() {

    }

    public void addProject() {

    }

    @PostMapping("add-employee")
    @ApiOperation("Add employee to project")
    public void addEmployeeToProject(@RequestBody @Valid EmployeeProjectRequest request) {
        LOGGER.info("Add employee to project. Request: {}", request);

        this.projectsService.addEmployeeToProject(request);
    }

    public void updateProject() {

    }

    public void closeProject() {

    }
}