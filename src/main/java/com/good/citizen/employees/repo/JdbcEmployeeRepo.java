package com.good.citizen.employees.repo;

import com.good.citizen.employees.model.Employee;
import com.good.citizen.employees.model.Team;
import com.good.citizen.employees.shared.JobTitle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Set;

@Repository
public class JdbcEmployeeRepo {

    private final JdbcTemplate jdbcTemplate;

    private static final String query = "SELECT * FROM employees.employee WHERE id = ?";

    public JdbcEmployeeRepo(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void doSomething() {
        System.out.println("-".repeat(10));
        System.out.println("Fetching EMPLOYEE");
        System.out.println("-".repeat(10));

        var employee = this.jdbcTemplate.queryForObject(query, (rs, rowNum) -> {
            var id = rs.getLong("id");
            var socialSecurityNumber = rs.getLong("social_security_number");
            var firstName = rs.getString("first_name");
            var lastName = rs.getString("last_name");
            var jobTitle = JobTitle.valueOf(rs.getString("job_title"));
            var teamId = rs.getLong("team_id");

            return new Employee(id, socialSecurityNumber, firstName, lastName, jobTitle, new Team(teamId, "whatever"), Set.of());
        }, 999);

        System.out.println(employee);
    }
}