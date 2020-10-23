package com.good.citizen.employees.repo;

import com.good.citizen.employees.repo.entity.ProjectEntity;
import com.good.citizen.shared.RepoUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class ProjectRepo {

    private final EntityManager em;

    public ProjectRepo(EntityManager em) {
        this.em = em;
    }

    public Optional<ProjectEntity> getProject(Long id) {
        var result = this.em.createQuery("SELECT p FROM ProjectEntity p where p.id = :id", ProjectEntity.class)
                .setParameter("id", id)
                .getResultList();

        return RepoUtils.fromResultListToOptional(result);
    }
}