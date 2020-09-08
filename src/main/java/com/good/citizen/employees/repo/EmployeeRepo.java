package com.good.citizen.employees.repo;

import com.good.citizen.employees.repo.entity.EmployeeEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepo {

    private final EntityManager em;

    public EmployeeRepo(EntityManager em) {
        this.em = em;
    }

    public List<EmployeeEntity> getAllEmployees() {
        return em.createQuery("""
                SELECT e FROM EmployeeEntity e
                JOIN FETCH e.team t""", EmployeeEntity.class)
                .getResultList();
    }

    public Optional<EmployeeEntity> getEmployee(Long id) {
        var employees = em.createQuery("""
                SELECT e FROM EmployeeEntity e
                JOIN FETCH e.projects p
                JOIN FETCH e.team t
                WHERE e.id = :id""", EmployeeEntity.class)
                .setParameter("id", id)
                .getResultList();

        return Optional.ofNullable(employees.isEmpty() ? null : employees.get(0));
    }

    public void save(EmployeeEntity employee) {
        this.em.persist(employee);
    }
}