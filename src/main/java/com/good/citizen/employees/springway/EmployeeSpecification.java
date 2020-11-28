package com.good.citizen.employees.springway;

import com.good.citizen.employees.api.request.EmployeeFilter;
import com.good.citizen.employees.repo.entity.EmployeeEntity;
import com.good.citizen.employees.shared.JobTitle;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public class EmployeeSpecification {

    public static Specification<EmployeeEntity> createEmployeeSpecification(EmployeeFilter filter) {
        return jobTitleIs(filter.getJobTitle())
                .and(hasTeamName(filter.getTeam()));
    }

    private static Specification<EmployeeEntity> jobTitleIs(Optional<JobTitle> jobTitle) {
        if (jobTitle.isEmpty()) {
            return ((root, criteriaQuery, criteriaBuilder) -> null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("jobTitle"), jobTitle.get());

        //You could write like so:
//        return (root, criteriaQuery, criteriaBuilder) -> jobTitle.map(title -> criteriaBuilder.equal(root.get("jobTitle"), title)).orElse(null);
    }

    private static Specification<EmployeeEntity> hasTeamName(Optional<String> maybeTeamName) {
        if (maybeTeamName.isEmpty()) {
            return ((root, criteriaQuery, criteriaBuilder) -> null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> {
            var team = root.join("team");
            return criteriaBuilder.equal(criteriaBuilder.lower(team.get("name")), maybeTeamName.get().toLowerCase());
        };
    }
}