## How to launch

Either using docker compose or docker or kubernetes.

```
mvn clean install
```



TODO
kubernetes


## Goal

This is an example of how good application should look like.

## Endpoints

1. Endpoints are mainly for documenting, logging and returning responses. They should contain as little business logic as possible. All business logic should be placed in Service Layer.
2. Use ``@ApiOperation`` to describe an endpoint's purpose. There is no need to describe ``response`` or ``responseContainer`` as it is inferred by SpringFox automatically.
3. Request and Response models should be immutable objects and have:
* ``private final`` instance variables;
* ``all-args`` constructor with ``@JsonCreator``;
* ``hashCode``, ``equals``, ``toString``;
This is for easier migration to [records](https://openjdk.java.net/jeps/359)
4. Use Builder pattern for constructing immutable objects. Future Java version will have [companion builder](https://mail.openjdk.java.net/pipermail/amber-spec-experts/2020-July/002236.html).
For now, use something like https://github.com/Randgalt/record-builder or write a builder by hand. My advice is against Lombok.

https://youtrack.jetbrains.com/issue/IDEA-248146

A more recent update on [record builders](https://github.com/openjdk/amber-docs/blob/master/eg-drafts/reconstruction-records-and-classes.md).
Possible syntax:
```
p with { x = 3; }
```

5. Whenever you have Collections of objects, don't forget to annotate ``@NotNull`` for the container type. For example:
```
DON'T
@NotNull
List<Integer> numbers;

DO
@NotNull
List<@NotNull Integer> numbers;
```
The difference is that in first example, request can be sent with ``"numbers": [null]`` and will be a valid request. 

6. Don't forget ``@Validated`` on ``@RestController``. For example if we have:
```
    @GetMapping("{id}")
    @ApiOperation("Get information about one employees")
    public void getEmployee(@PathVariable("id") @Min(1) Long id) { <----------------- @Min
        LOGGER.info("Get all employee request. Employee id: {}", id);

        employeeService.getEmployee();
    }
```

and if ```@Validated``` is missing, then ```@Min``` will not be taken into account, thus you could pass -1 as an ``id``.

7. If you want to have filters for endpoints, like so:
```
    @GetMapping
    @ApiOperation("Get information about all employees")
    public Set<Employee> getAllEmployees(EmployeeFilter employeeFilter) {
        LOGGER.info("Get all employees request. Employee filter {}", employeeFilter);

        return this.employeeService.getAllEmployees(employeeFilter);
    }
```

Make sure to use ```@JsonCreator``` and getters, because Spring does not support Records for request params yet. 


## Repository

1. Use ``getResultList()`` vs ``getSingleResult()``, because ``getSingleResult()`` throws ``NoResultException`` - if there is no result, thus you would have to
wrap it into ``try/catch``.

```
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
```

2. Prefer JPQL versus Criter builder.

```
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
```

VS 

```
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
```

Example of dynamic queries can be found in ``EmployeeRepo`` and ``EmployeeRepoCriteria``.


### Entities

``equals`` and ``hashCode`` is implemtend according to Vlad Mihalcea. More information can be found in *High-Performance Java Persistence* or in this [link](https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/).

If entity can return null value, wrap *getter* into ``Optional``.

``Entities`` should not leave Service layer. Always return a *view* of ``Entity`` instead. 

## Optional and nullability

Avoid nulls at all cost! Developer has to trust it's own code so you wouldn't require to sprinkle ```Objects.requireNonNull``` or worse ```if (object != null)``` everywhere.
Defend at the perimeter! All data which is incoming into the system from outside (REST request, RabbitMQ messages, Database entities etc.) have to follow these rules regarding nullability:
* If property can be null, you should use Optional as a return **parameter**.
```
public class ExampleRequest {
    
    private final String team;

    public ExampleRequest(String team) {
        this.team = team;
    }

    public Optional<String> getTeam() {
        return Optional.ofNullable(team);
    }
}
```
* If property can have default value, define it in the constructor. Another reason to use immutable types. They guarantee correct values.
Example:
```
    @JsonCreator
    public EmployeeRequest(String name, String lastName, String team, JobTitle jobTitle) {
        this.name = name;
        this.lastName = lastName;
        this.team = Objects.requireNonNullElse(team, "Special Team");
        this.jobTitle = jobTitle;
    }
```
Here, for example, team can be null. However, if we can define a default value for that property, we should do it using ```requireNonNullElse``` or ```requireNonNullElseGet```.

## Tests

### Naming

Follow test method naming conventions ``methodUnderTest__[given/when]__then`` where ``given/when`` is optional. 
Example: 
* ``getAllEmployees__thenReturnListOfEmployees``
* ``getAllEmployees__whenNoEmployeesExist__thenReturnEmptyList``
* ``getEmployee__whenInvalidEmployeeId__thenReturn400``
* ``getEmployee__whenEmployeeDoesNotExists__thenReturn404``


## Maven


## HTTP Client


## No Async/await frameworks

ReactorX and friends.

## Application.yaml


## Kubernetes pod template

## Database migration