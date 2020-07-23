package com.good.citizen.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.good.citizen.shared.JobTitle;

import java.util.Objects;
import java.util.Optional;

public class EmployeeRequest {

    private final String name;
    private final JobTitle jobTitle;
    private final Integer age;
    private final String previousTitle;

    @JsonCreator
    public EmployeeRequest(String name, JobTitle jobTitle, Integer age, String previousTitle) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.age = age;
        this.previousTitle = previousTitle;
    }

    public String getName() {
        return name;
    }

    public JobTitle getJobTitle() {
        return jobTitle;
    }

    public Integer getAge() {
        return age;
    }

    public Optional<String> getPreviousTitle() {
        return Optional.ofNullable(previousTitle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeRequest that = (EmployeeRequest) o;
        return Objects.equals(name, that.name) &&
                jobTitle == that.jobTitle &&
                Objects.equals(age, that.age) &&
                Objects.equals(previousTitle, that.previousTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, jobTitle, age, previousTitle);
    }

    @Override
    public String toString() {
        return "EmployeeRequest{" +
                "name='" + name + '\'' +
                ", jobTitle=" + jobTitle +
                ", age=" + age +
                ", previousTitle='" + previousTitle + '\'' +
                '}';
    }
}