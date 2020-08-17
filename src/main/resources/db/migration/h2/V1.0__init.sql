CREATE TABLE team
(
    id   BIGINT NOT NULL identity (1,1),
    name VARCHAR(255),

    CONSTRAINT PK__team__id PRIMARY KEY (id)
);

CREATE TABLE project
(
    id   BIGINT       NOT NULL identity (1,1),
    name VARCHAR(255) NOT NULL,

    CONSTRAINT PK__project__id PRIMARY KEY (id)
);

CREATE TABLE employee
(
    id         BIGINT       NOT NULL identity (1,1),
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    position   VARCHAR(255) NOT NULL,
    team_id    BIGINT       NOT NULL,

    CONSTRAINT PK__employee__id PRIMARY KEY (id),
    CONSTRAINT FK__employee__team FOREIGN KEY (team_id) REFERENCES team (id)
);

CREATE TABLE employee_project
(
    employee_id BIGINT,
    project_id  BIGINT,

    CONSTRAINT PK__employee_project__employee_id_project_id PRIMARY KEY (employee_id, project_id),
    CONSTRAINT FK__employee_project__employee FOREIGN KEY (employee_id) REFERENCES employee (id),
    CONSTRAINT FK__employee_project__project FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE sales
(
    id                  BIGINT         NOT NULL identity (1,1),
    car_name            VARCHAR(255)   NOT NULL,
    price               DECIMAL(19, 2) NOT NULL,
    discount            DECIMAL(19, 2),
    contact_information VARCHAR(500),
    employee_id         BIGINT         NOT NULL,

    CONSTRAINT PK__sales__id PRIMARY KEY (id),
    CONSTRAINT FK__sales__employee FOREIGN KEY (employee_id) REFERENCES employee (id)
);