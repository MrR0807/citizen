package com.good.citizen.employees.repo;

import com.good.citizen.employees.api.request.EmployeeFilter;
import com.good.citizen.employees.repo.entity.EmployeeEntity;
import com.good.citizen.employees.repo.entity.TeamEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeRepoSpringData extends JpaRepository<EmployeeEntity, Long>, JpaSpecificationExecutor<EmployeeEntity> {

    @Override
//    @Query("""
//            SELECT e FROM EmployeeEntity e
//                            JOIN FETCH e.projects p
//                            JOIN FETCH e.team t
//                            WHERE e.id = :id""")
    Optional<EmployeeEntity> findById(Long id);

    default Example<EmployeeEntity> from(EmployeeFilter filter) {
        var employeeEntity = new EmployeeEntity();

        if (filter.getTeam().isPresent() && filter.getJobTitle().isPresent()) {
            var teamEntity = new TeamEntity();
            teamEntity.setName(filter.getTeam().get());
            employeeEntity.setTeam(teamEntity);
            employeeEntity.setJobTitle(filter.getJobTitle().get());
        } else if (filter.getTeam().isPresent()) {
            var teamEntity = new TeamEntity();
            teamEntity.setName(filter.getTeam().get());
            employeeEntity.setTeam(teamEntity);
        } else if (filter.getJobTitle().isPresent()) {
            employeeEntity.setJobTitle(filter.getJobTitle().get());
        }

        return Example.of(employeeEntity);
    }
}