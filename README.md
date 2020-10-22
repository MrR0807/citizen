## How to launch

Either using docker compose, docker or kubernetes.

```
mvn clean install
```

TODO
kubernetes


## Goal

This is an example of how good (in my opinion) application should look like.

## Lombok

**Do not use Lombok**. [Records](https://openjdk.java.net/jeps/359) + [withers](https://github.com/openjdk/amber-docs/blob/master/eg-drafts/reconstruction-records-and-classes.md)
should cover almost all cases.

```@Entity``` classes will consist of getters and setters boilerplate, but they make up a very small parts of your application. Plus, I've seen the same mistake done many times - 
```@Entity``` class marked with ```@Data``` or  ```@EqualsAndHashCode``` leads to bad JPA practices:
* Missing bidirectional synchronized methods.
> Whenever a bidirectional association is formed, the application developer must make sure both sides are in-sync at all times.
The addPhone() and removePhone() are utility methods that synchronize both ends whenever a child element is added or removed.

[Source - Hibernate User Guide](https://docs.jboss.org/hibernate/stable/orm/userguide/html_single/Hibernate_User_Guide.html#associations-one-to-many-bidirectional)

>However, we still need to have both sides in sync as otherwise, we break the Domain Model relationship consistency, and the entity state transitions are not guaranteed to work unless both sides are properly synchronized.

[Source - Vlad Mihalcea Blog](https://vladmihalcea.com/jpa-hibernate-synchronize-bidirectional-entity-associations/)

* Bad equals and hashcode implementations. [Vlad Michalcea Blog](https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/)

#### Additional Arguments

> Spring and Hibernate generate bytecode the way the compiler is intending to support it: by generating a java source code file and then compiling it without touching the source file/class 
>the annotation came from. Lombok goes into the parse tree (via non-public APIs) and mutates the AST in place.
- Reddit comment

> <...> while we do code against internal API, they are relatively stable bits. If what lombok does could be done without resorting to internal API, we'd have done something else, 
>but it can't be done, so we resort to internal API usage.
- [Lombok developers](https://stackoverflow.com/questions/6107197/how-does-lombok-work):

More resources:
* https://youtrack.jetbrains.com/issue/IDEA-248146
* https://github.com/rzwitserloot/lombok/issues/1723
* https://github.com/jhipster/generator-jhipster/issues/398
* https://medium.com/@vgonzalo/dont-use-lombok-672418daa819
* https://medium.com/@gabor.liptak/some-dangers-of-using-lombok-d759fc8f701f
* https://paluch.biz/blog/180-data-classes-considered-harmful.html
* http://gregorriegler.com/2019/08/10/who-needs-lombok-anyhow.html

## Endpoints

Endpoints are mainly for documenting, logging requests and returning responses. They should contain as little business logic as possible. All business logic should be placed in Service Layer.

----

Use ``@ApiOperation`` to describe an endpoint's purpose. There is no need to describe ``response`` or ``responseContainer`` as it is inferred by SpringFox automatically.

----

Request and Response models should be immutable objects and have (This is for easier migration to [records](https://openjdk.java.net/jeps/359)):
* ``private final`` instance variables;
* ``all-args`` constructor with ``@JsonCreator``;
* ``hashCode``, ``equals``, ``toString``;

Use Builder pattern for constructing immutable objects.

----

**Whenever you have Collections of objects, don't forget to annotate ``@NotNull`` for the container type.** For example:

```
BAD
@NotNull
List<Integer> numbers;

GOOD
@NotNull
List<@NotNull Integer> numbers;
```

The difference is that in first example, request can be sent with ``"numbers": [null]`` and will be a valid request.

---- 

**Don't forget ``@Validated`` on ``@RestController``**. 

Example:
```
    @GetMapping("{id}")
    @ApiOperation("Get information about one employees")
    public void getEmployee(@PathVariable("id") @Min(1) Long id) { <----------------- @Min
        LOGGER.info("Get all employee request. Employee id: {}", id);

        employeeService.getEmployee();
    }
```

If ```@Validated``` is missing, then ```@Min``` will not be taken into account, thus you could pass -1 as an ``id``.

----

If you want to have filters for endpoints, like so:
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

#### Use ``getResultList()`` vs ``getSingleResult()``

``getSingleResult()`` throws ``NoResultException`` - if there is no result, thus you would have to wrap it into ``try/catch``.

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

#### Prefer JPQL versus Criteria builder.

JPQL:
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

CriteriaBuilder:
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

VS

Spring Data:
```
    @Override
    @Query("""
            SELECT e FROM EmployeeEntity e
                            JOIN FETCH e.projects p
                            JOIN FETCH e.team t
                            WHERE e.id = :id""")
    Optional<EmployeeEntity> findById(Long id);
```

We need to define JPQL query, because Spring cannot join tables optimally, unless you define ``fetch = FetchType.EAGER``. With ManyToMany relationship,
this can lead to N+1. 

Example of dynamic queries can be found in ``EmployeeRepo`` and ``EmployeeRepoCriteria``.
A more elaborate comparison is in ``getAllEmployees`` where filter is given as an input parameter.
In this particular case Spring Data's ``Specification`` API is not useful when data is being fetched with ``JOINS``. 
However, I have created ``EmployeeRepoSpringData``, using ``Example`` API. Unfortunately it does not work if you want to fetch associations in your 
query instead of allowing N+1 problem via ``FetchType.EAGER``.

### Entities

``equals`` and ``hashCode`` is implemented according to Vlad Mihalcea. More information can be found in *High-Performance Java Persistence* or in this [link](https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/).

If entity can return null value, wrap *getter* into ``Optional``.

``Entities`` should not leave Service layer. Always return a *view* of ``Entity`` instead.

Bidirectional synchronized methods.
> Whenever a bidirectional association is formed, the application developer must make sure both sides are in-sync at all times.
The addPhone() and removePhone() are utility methods that synchronize both ends whenever a child element is added or removed:

[Source - Hibernate User Guide](https://docs.jboss.org/hibernate/stable/orm/userguide/html_single/Hibernate_User_Guide.html#associations-one-to-many-bidirectional)

>However, we still need to have both sides in sync as otherwise, we break the Domain Model relationship consistency, and the entity state transitions are not guaranteed to work unless both sides are properly synchronized.

[Source - Vlad Mihalcea Blog](https://vladmihalcea.com/jpa-hibernate-synchronize-bidirectional-entity-associations/)

## Optional and nullability

Avoid nulls at all cost! Developer has to trust code otherwise you will find this pattern sprinkled ```Objects.requireNonNull``` or worse ```if (object != null)``` everywhere.
Defend at the perimeter! All data which is incoming into the system from outside (REST request, RabbitMQ messages, Database entities etc.) has to follow these rules regarding nullability:
* If property can be null, you should use Optional as a return **parameter**.

Defending at the perimeter: https://youtu.be/wY_CUkU1zfw?t=2800
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

Follow test method naming conventions ``methodUnderTest__[given/when]__[then]`` where ``given/when`` and ``then`` is optional. 
Example: 
* ``getAllEmployees__thenReturnListOfEmployees``
* ``getAllEmployees__whenNoEmployeesExist__thenReturnEmptyList``
* ``getEmployee__whenInvalidEmployeeId__thenReturn400``
* ``getEmployee__whenEmployeeDoesNotExists__thenReturn404``

### Integration Tests

Reuse Spring's Application Context as much as possible. This will speed up your tests, because then it doesn't restart, thus in perfect example, you only need to start Spring's Application once.
Avoid @MockBeans.

Either created a single annotation or class which each integration test extends.

### Resources

https://www.aerokhin.com/2020/09/28/what-is-the-right-unit-in-unit-test-after-all/

## Spring

Dependencies should be inject by constructor. From Spring documentation:
> The Spring team generally advocates constructor injection, as it lets you implement application components as immutable objects and ensures 
>that required dependencies are not null. Furthermore, constructor-injected components are always returned to the client (calling) code in a fully initialized state.

----

If you have multiple properties, prefer to use [Type-safe Configuration Properties](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config-typesafe-configuration-properties).

```
@ConstructorBinding
@ConfigurationProperties("acme")
public class AcmeProperties {

    private final boolean enabled;
    private final int remoteAddress;
    private final String name

    public AcmeProperties(boolean enabled, int remoteAddress, String security) {
        this.enabled = enabled;
        this.remoteAddress = remoteAddress;
        this.security = security;
    }

    //Getters
}
``` 

----

No need to declare ```@Autowired``` on constructors if only one constructor exists.

## Maven

1. Try to have as little external dependencies as possible. Every dependency might block you in the future from migrating to newer Java version.
2. Don't over-engineer plugins.
3. At least in my experience it's best to have maven-compiler-plugin declared with Java versions, otherwise IntelliJ sets wrong Java for the project:
```
<!--plugin for intellij to get settings-->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <release>15</release>
        <compilerArgs>--enable-preview</compilerArgs>
        <forceJavacCompilerUse>true</forceJavacCompilerUse>
        <parameters>true</parameters>
    </configuration>
</plugin>
```  

## HTTP Client

## No Async/await frameworks

ReactorX and friends.

## Application.yaml

## Kubernetes pod template

## Database migration

## Git

What happens when tag is equal to branch name? It will checkout to tag, instead of branch. That means, that new code has been commited to branch and CI tool
would like to checkout to the head of named branch (using branch name), it will checkout to tag and you won't have your changes deployed.