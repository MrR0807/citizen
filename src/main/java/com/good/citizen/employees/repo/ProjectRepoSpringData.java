package com.good.citizen.employees.repo;

import com.good.citizen.employees.repo.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

//For demo purpose only
public interface ProjectRepoSpringData extends JpaRepository<ProjectEntity, Long> {
}