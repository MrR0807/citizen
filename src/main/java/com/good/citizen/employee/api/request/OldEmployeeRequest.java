package com.good.citizen.employee.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.good.citizen.employee.shared.JobTitle;

import java.util.Objects;
import java.util.Optional;

//This is only for showcasing why it is important to have immutable data classes. This is essentially what records are underneath
public class OldEmployeeRequest {

    private final String name;
    private final JobTitle jobTitle;
    private final Integer age;
    private final String previousTitle;

    @JsonCreator
    public OldEmployeeRequest(String name, JobTitle jobTitle, Integer age, String previousTitle) {
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
        OldEmployeeRequest that = (OldEmployeeRequest) o;
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