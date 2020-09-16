package com.good.citizen.employees.repo;

import com.good.citizen.employees.api.request.EmployeeFilter;
import com.good.citizen.employees.repo.entity.EmployeeEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@Repository
public class EmployeeRepo {

    private final EntityManager em;

    public EmployeeRepo(EntityManager em) {
        this.em = em;
    }

    public List<EmployeeEntity> getAllEmployees(EmployeeFilter filter) {
        var sql = new StringBuilder("SELECT e FROM EmployeeEntity e JOIN FETCH e.team t");

        this.addPredicatesToSql(filter, sql);
        var typedQuery = this.em.createQuery(sql.toString(), EmployeeEntity.class);
        setTypedQueryParameters(filter, typedQuery);

        return typedQuery.getResultList();
    }

    private void addPredicatesToSql(EmployeeFilter filter, StringBuilder sql) {
        if (anyFilterIsPresent(filter)) {
            sql.append(" WHERE ");
            var predicates = Stream.of(jobTitleFilter(filter), teamFilter(filter))
                    .filter(not(String::isBlank))
                    .collect(Collectors.joining(" AND "));
            sql.append(predicates);
        }
    }

    private static boolean anyFilterIsPresent(EmployeeFilter filter) {
        return filter.getJobTitle().isPresent() || filter.getTeam().isPresent();
    }

    private static String jobTitleFilter(EmployeeFilter filter) {
        return filter.getJobTitle().map(jobTitle -> "e.jobTitle = :jobTitle").orElse("");
    }

    private static String teamFilter(EmployeeFilter filter) {
        return filter.getTeam().map(team -> "LOWER(t.name) = :teamName").orElse("");
    }

    private static void setTypedQueryParameters(EmployeeFilter filter, TypedQuery<EmployeeEntity> typedQuery) {
        if (anyFilterIsPresent(filter)) {
            filter.getTeam().ifPresent(team -> typedQuery.setParameter("teamName", team.toLowerCase()));
            filter.getJobTitle().ifPresent(jobTitle -> typedQuery.setParameter("jobTitle", jobTitle));
        }
    }

    public Optional<EmployeeEntity> getEmployee(Long id) {
        var employees = this.em.createQuery("""
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