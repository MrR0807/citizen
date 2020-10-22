package com.good.citizen.employees.repo;

import com.good.citizen.employees.repo.entity.TeamEntity;
import com.good.citizen.shared.RepoUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class TeamRepo {

    private final EntityManager em;

    public TeamRepo(EntityManager em) {
        this.em = em;
    }

    public Optional<TeamEntity> getTeam(Long id) {
        var result = this.em.createQuery("SELECT t FROM TeamEntity t where t.id = :id", TeamEntity.class)
                .setParameter("id", id)
                .getResultList();

        return RepoUtils.fromResultListToOptional(result);
    }

    public Optional<TeamEntity> getTeam(String name) {
        var result = this.em.createQuery("SELECT t FROM TeamEntity t where t.name = :name", TeamEntity.class)
                .setParameter("name", name)
                .getResultList();

        return RepoUtils.fromResultListToOptional(result);
    }

    public void save(TeamEntity teamEntity) {
        this.em.persist(teamEntity);
    }
}