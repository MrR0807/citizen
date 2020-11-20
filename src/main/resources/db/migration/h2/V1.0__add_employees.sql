CREATE SCHEMA employees;

-------------------------------- Add Tables --------------------------------

CREATE TABLE employees.team
(
    id   BIGINT       NOT NULL identity (1,1),
    name VARCHAR(255) NOT NULL,

    CONSTRAINT PK__team__id PRIMARY KEY (id)
);

CREATE TABLE employees.project
(
    id     BIGINT       NOT NULL identity (1,1),
    name   VARCHAR(255) NOT NULL,
    budget DECIMAL(19, 2),

    CONSTRAINT PK__project__id PRIMARY KEY (id)
);

CREATE TABLE employees.employee
(
    id                     BIGINT       NOT NULL identity (1,1),
    social_security_number BIGINT       NOT NULL,
    first_name             VARCHAR(255) NOT NULL,
    last_name              VARCHAR(255) NOT NULL,
    job_title              VARCHAR(255) NOT NULL,
    team_id                BIGINT       NOT NULL,

    CONSTRAINT PK__employee__id PRIMARY KEY (id),
    CONSTRAINT FK__employee__team FOREIGN KEY (team_id) REFERENCES team (id),
    CONSTRAINT UQ__social_security_number UNIQUE (social_security_number)
);

CREATE TABLE employees.employee_project
(
    employee_id BIGINT,
    project_id  BIGINT,

    CONSTRAINT PK__employee_project__employee_id_project_id PRIMARY KEY (employee_id, project_id),
    CONSTRAINT FK__employee_project__employee FOREIGN KEY (employee_id) REFERENCES employee (id),
    CONSTRAINT FK__employee_project__project FOREIGN KEY (project_id) REFERENCES project (id)
);

-------------------------------- Add Data --------------------------------

INSERT INTO employees.team (id, name)
VALUES (1, 'Blue'),
       (2, 'Red'),
       (3, 'Green');

INSERT INTO employees.project (id, name, budget)
VALUES (1, 'One', 1000.0),
       (2, 'Z', 1000000.0),
       (3, 'X', null);

INSERT INTO employees.employee (id, social_security_number, first_name, last_name, job_title, team_id)
VALUES (1, 123456789, 'First', 'Lastname', 'SOFTWARE_DEVELOPER', 1),
       (2, 987654321, 'Second', 'Lastname', 'SOFTWARE_DEVELOPER', 2),
       (3, 111444777, 'Third', 'Lastname', 'BUSINESS_ANALYST', 1),
       (4, 222555888, 'Fourth', 'Lastname', 'PRODUCT_OWNER', 1);

INSERT INTO employees.employee_project(employee_id, project_id)
VALUES (1, 1),
       (2, 3),
       (3, 1),
       (4, 1);