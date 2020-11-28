package com.good.citizen.employees.springway;

import com.good.citizen.employees.repo.entity.EmployeeEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepoSpringData extends JpaRepository<EmployeeEntity, Long>, JpaSpecificationExecutor<EmployeeEntity> {

//    @Override
//    @Query("""
//            SELECT e FROM EmployeeEntity e
//                            JOIN FETCH e.projects p
//                            JOIN FETCH e.team t
//                            WHERE e.id = :id""")
//    Optional<EmployeeEntity> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"team", "projects"})
    Optional<EmployeeEntity> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<EmployeeEntity> findAll(Specification<EmployeeEntity> specification);
}