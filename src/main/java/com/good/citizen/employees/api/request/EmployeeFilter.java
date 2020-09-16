package com.good.citizen.employees.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.good.citizen.employees.shared.JobTitle;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class EmployeeFilter {

    private final String team;
    private final JobTitle jobTitle;

    @JsonCreator
    public EmployeeFilter(String team, JobTitle jobTitle) {
        this.team = StringUtils.normalizeSpace(team);
        this.jobTitle = jobTitle;
    }

    public Optional<String> getTeam() {
        return Optional.ofNullable(this.team);
    }

    public Optional<JobTitle> getJobTitle() {
        return Optional.ofNullable(this.jobTitle);
    }

    @Override
    public String toString() {
        return "EmployeeFilter{" +
                "team='" + this.team + '\'' +
                ", jobTitle=" + this.jobTitle +
                '}';
    }
}