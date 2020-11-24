package com.good.citizen.employees.api.request;

import com.good.citizen.employees.shared.JobTitle;
import com.good.citizen.shared.PatchField;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Min;

public class PatchEmployeeRequest {

    private PatchField<@Min(0) Long> socialSecurityNumber = PatchField.empty();
    private PatchField<String> firstName = PatchField.empty();
    private PatchField<String> lastName = PatchField.empty();
    private PatchField<String> team = PatchField.empty();
    private PatchField<JobTitle> jobTitle = PatchField.empty();

    public PatchField<Long> getSocialSecurityNumber() {
        return this.socialSecurityNumber;
    }

    public void setSocialSecurityNumber(Long socialSecurityNumber) {
        this.socialSecurityNumber = new PatchField<>(socialSecurityNumber);
    }

    public PatchField<String> getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = new PatchField<>(StringUtils.normalizeSpace(firstName));
    }

    public PatchField<String> getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = new PatchField<>(StringUtils.normalizeSpace(lastName));
    }

    public PatchField<String> getTeam() {
        return this.team;
    }

    public void setTeam(String team) {
        this.team = new PatchField<>(StringUtils.normalizeSpace(team));
    }

    public PatchField<JobTitle> getJobTitle() {
        return this.jobTitle;
    }

    public void setJobTitle(JobTitle jobTitle) {
        this.jobTitle = new PatchField<>(jobTitle);
    }

    @Override
    public String toString() {
        return "PatchEmployeeRequest{" +
                "socialSecurityNumber=" + this.socialSecurityNumber +
                ", firstName=" + this.firstName +
                ", lastName=" + this.lastName +
                ", team=" + this.team +
                ", jobTitle=" + this.jobTitle +
                '}';
    }
}