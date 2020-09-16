package com.good.citizen.employees.repo;

import com.good.citizen.employees.api.request.EmployeeFilter;
import com.good.citizen.employees.repo.entity.EmployeeEntity;
import com.good.citizen.employees.shared.JobTitle;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepoCriteria {

    private final EntityManager em;

    public EmployeeRepoCriteria(EntityManager em) {
        this.em = em;
    }

    public List<EmployeeEntity> getAllEmployees(EmployeeFilter filter) {
        var cb = this.em.getCriteriaBuilder();
        var query = cb.createQuery(EmployeeEntity.class);
        var employeeEntity = query.from(EmployeeEntity.class);
        query.select(employeeEntity);
        //Yes this is required. Otherwise in ``definePredicates`` method, I cannot do ``teamEntity.get`` 
        var teamEntity = (Join<Object, Object>) employeeEntity.fetch("team");

        definePredicates(filter, cb, query, employeeEntity, teamEntity);
        var typedQuery = this.em.createQuery(query);
        setTypedQueryParameters(filter, typedQuery);

        return typedQuery.getResultList();
    }

    private static void definePredicates(EmployeeFilter filter, CriteriaBuilder cb, CriteriaQuery<EmployeeEntity> query,
                                         Root<EmployeeEntity> employeeEntity, Join<Object, Object> teamEntity) {
        var predicates = new ArrayList<Predicate>();

        if (filter.getTeam().isPresent()) {
            var p = cb.parameter(String.class, "teamName");
            predicates.add(cb.equal(cb.lower(teamEntity.get("name")), p));
        }
        if (filter.getJobTitle().isPresent()) {
            var p = cb.parameter(JobTitle.class, "jobTitle");
            predicates.add(cb.equal(employeeEntity.get("jobTitle"), p));
        }

        if (predicates.size() == 1) {
            query.where(predicates.get(0));
        } else if (predicates.size() > 1) {
            query.where(cb.and(predicates.toArray(Predicate[]::new)));
        }
    }

    private static void setTypedQueryParameters(EmployeeFilter filter, javax.persistence.TypedQuery<EmployeeEntity> typedQuery) {
        if (anyFilterIsPresent(filter)) {
            filter.getTeam().ifPresent(team -> typedQuery.setParameter("teamName", team.toLowerCase()));
            filter.getJobTitle().ifPresent(jobTitle -> typedQuery.setParameter("jobTitle", jobTitle));
        }
    }

    private static boolean anyFilterIsPresent(EmployeeFilter filter) {
        return filter.getJobTitle().isPresent() || filter.getTeam().isPresent();
    }

    public Optional<EmployeeEntity> getEmployee(Long id) {
        var cb = this.em.getCriteriaBuilder();
        var query = cb.createQuery(EmployeeEntity.class);
        var employeeEntity = query.from(EmployeeEntity.class);
        employeeEntity.fetch("team");
        employeeEntity.fetch("projects");
        query.select(employeeEntity)
                .where(cb.equal(employeeEntity.get("id"), id));

        var employees = this.em.createQuery(query).getResultList();

        return Optional.ofNullable(employees.isEmpty() ? null : employees.get(0));
    }
}