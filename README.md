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

```@Entity``` classes will consist of getters and setters boilerplate, but they make up a very small part of your application, and does not justify adding Lombok. 
If you're truly bothered by getters/setters you can just make properties ``public``. 

#### Main Arguments

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

#### Lombok and Entity

I've seen the same mistake done many times - ```@Entity``` class marked with ```@Data``` or  ```@EqualsAndHashCode``` leads to bad JPA practices:
* Missing bidirectional synchronized methods.
> Whenever a bidirectional association is formed, the application developer must make sure both sides are in-sync at all times.
The addPhone() and removePhone() are utility methods that synchronize both ends whenever a child element is added or removed.

[Source - Hibernate User Guide](https://docs.jboss.org/hibernate/stable/orm/userguide/html_single/Hibernate_User_Guide.html#associations-one-to-many-bidirectional)

>However, we still need to have both sides in sync as otherwise, we break the Domain Model relationship consistency, and the entity state transitions are not guaranteed to work unless both sides are properly synchronized.

[Source - Vlad Mihalcea Blog](https://vladmihalcea.com/jpa-hibernate-synchronize-bidirectional-entity-associations/)

* Bad equals and hashcode implementations. [Vlad Michalcea Blog](https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/)

## Endpoints

Controller classes are mainly for documenting endpoint, logging requests then delegating to Service Layer and returning responses. 
They should contain as little business logic as possible.

```
    @GetMapping("{id}")
    @ApiOperation("Get information about one employees")
    public Employee getEmployee(@PathVariable("id") @Min(1) Long id) {
        LOGGER.info("Get employee request. Employee id: {}", id);

        return this.employeeService.getEmployee(id);
    }
```

---

Don't use ``ResponseEntity`` boilerplate:
```
    @GetMapping("{id}")
    @ApiOperation("Get information about one employees")
    public ResponseEntity<Employee> getEmployee(@PathVariable("id") @Min(1) Long id) {
        LOGGER.info("Get employee request. Employee id: {}", id);

        var employee = this.employeeService.getEmployee(id);
        return ResponseEntity.ok(employee);
    }
```

Just return the object:
```
    @GetMapping("{id}")
    @ApiOperation("Get information about one employees")
    public Employee getEmployee(@PathVariable("id") @Min(1) Long id) {
        LOGGER.info("Get employee request. Employee id: {}", id);

        return this.employeeService.getEmployee(id);
    }
```

----

Use ``@ApiOperation`` to describe an endpoint's purpose. There is no need to describe ``response`` or ``responseContainer`` as it is inferred by SpringFox automatically.

----

Request and Response models should be immutable objects and have (this is for easier migration to [records](https://openjdk.java.net/jeps/359)):
* ``private final`` instance variables;
* ``all-args`` constructor with ``@JsonCreator``;
* ``hashCode``, ``equals``, ``toString``;

Use Builder pattern for constructing immutable objects.

After Java 16+ use ``records``.

----

**Whenever you have Collections of objects, don't forget to annotate ``@NotNull`` for the container type:**

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

---

### HTTP Methods

GET, HEAD, OPTIONS, and TRACE methods are defined to be safe. Request methods are considered "safe" if their defined semantics are essentially **read-only**.

[Source](https://tools.ietf.org/html/rfc7231#section-4.2)

#### Idempotent Methods

PUT, DELETE, and safe request methods are idempotent.

Idempotent - operation can be repeated and the outcome will be the same (the server's state will remain the same). For example, PUT employee will either create or update existing. Doesn't matter,
how many times you will repeat the same operation it will always return same response.

The problem with DELETE, which if successful would normally return a 200 (OK) or 204 (No Content), will often return a 404 (Not Found) on subsequent calls. However, the state on the server 
is the same after each DELETE call, but the response is different.

---

#### GET

``GET /resources`` (possibility of filter which returns empty list)
``GET /resources/{resource-id}``
``GET /resources/{resource-name}``

If ``GET`` list does not contain resources (with/without filters) return an empty list or throw exception. **I prefer returning an empty list**.

Example of ``GET`` list:
```
    @GetMapping
    @ApiOperation("Get information about all employees")
    public Set<Employee> getAllEmployees(EmployeeFilter filter) {
        LOGGER.info("Get all employees request. Employee filter {}", filter);

        return this.employeeService.getAllEmployees(filter);
    }

//This will be record in the future
public class EmployeeFilter {

    private final String team;
    private final JobTitle jobTitle;

    @JsonCreator
    public EmployeeFilter(String team, JobTitle jobTitle) {
        this.team = StringUtils.normalizeSpace(team);
        this.jobTitle = jobTitle;
    }

    public Optional<String> getTeam() {
        return Optional.ofNullable(this.team);
    }

    public Optional<JobTitle> getJobTitle() {
        return Optional.ofNullable(this.jobTitle);
    }

    @Override
    public String toString() {
        return "EmployeeFilter{" +
                "team='" + this.team + '\'' +
                ", jobTitle=" + this.jobTitle +
                '}';
    }
}
```

Example of ``GET`` resource:
```
    @GetMapping("{id}")
    @ApiOperation("Get information about one employees")
    public Employee getEmployee(@PathVariable("id") @Min(1) Long id) {
        LOGGER.info("Get employee request. Employee id: {}", id);

        return this.employeeService.getEmployee(id);
    }
```

---

#### POST

``POST /employees``

If employee exists, throw exception - ``Bad Request (400)``.

Questions:
* How will respond look:
  * Return only OK;
  * Return OK and created resource's id;
  * Return OK and entire created resource (in some case this might be too costly).
* What unique properties define resource?

Example:
```
    @PostMapping
    @ApiOperation("Add employee")
    public void addEmployee(@RequestBody @Valid EmployeeRequest request) {
        LOGGER.info("Add employee. Employee: {}", request);

        this.employeeService.addEmployee(request);
    }

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EmployeeRequest(
        @NotNull Long socialSecurityNumber,
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 255) String lastName,
        @NotBlank @Size(max = 255) String team,
        @NotNull JobTitle jobTitle) {
}
```

#### PUT

> The PUT method requests that the state of the target resource be created or replaced with the state defined by the representation enclosed in the request message payload.

``PUT`` is idempotent.

Questions:
* What unique properties define resource?
* Will you respond differently when resource was created/updated?

> If the target resource does not have a current representation and the PUT successfully creates one, then the origin server MUST inform the user agent by sending a 201 (Created) response.  If the target
>resource does have a current representation and that representation is successfully modified in accordance with the state of the enclosed representation, then the origin server MUST send either a 200 (OK) or
>a 204 (No Content) response to indicate successful completion of the request.

#### PATCH

POST /resources (create) (if already exists throw 400)
GET /resources/{resource-id} (if doesn't exist throw 404)
POST /resources/{resource-id} (update, if doesn't exist throw 404)
PATCH /resources/{resource-id} (partial update, if doesn't exist throw 404)

PUT (?) Indeponent opperation. Create or update? If exists create or update? Always return 200?

### PATCH

https://tools.ietf.org/html/rfc7386


## Error Handling

Errors within your application should be protocol-independent. For example, if an Employee does not exist, application throws ``NotFoundException``.
Then depending on the protocol, may it be AMQP or HTTP, error should translate accordingly. For HTTP there are Spring's ``@RestControllerAdvice``.
I've provided a list of exception handlers which are utilized in my other applications. Find them under ``exceptionhandlers`` package.

## Repository

#### Use ``getResultList()`` vs ``getSingleResult()``

``getSingleResult()`` throws ``NoResultException`` which is ``RuntimeException`` - if there is no result, thus you would have to wrap it into ``try/catch``.
I advise utilizing ``getResultList()`` like so:

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

---

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
this [can lead to N+1 problem](https://vladmihalcea.com/n-plus-1-query-problem/). 

Example of dynamic queries can be found in ``EmployeeRepo`` and ``EmployeeRepoCriteria``.
A more elaborate comparison is in ``getAllEmployees`` where filter is given as an input parameter.
In this particular case Spring Data's ``Specification`` API is not useful when data is fetched with ``JOINS``. 
However, I have created ``EmployeeRepoSpringData``, using ``Example`` API. Unfortunately it does not work if you want to fetch associations in your 
query instead of allowing N+1 problem via ``FetchType.EAGER``.

### Entities

``equals`` and ``hashCode`` is implemented according to Vlad Mihalcea. More information can be found in *High-Performance Java Persistence* or in this [link](https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/).

---

If entity can return null value, wrap *getter* into ``Optional``.

---

``Entities`` should not leave Service layer. Always return a *view* of ``Entity`` instead.

---

Bidirectional synchronized methods.
> Whenever a bidirectional association is formed, the application developer must make sure both sides are in-sync at all times.
The addPhone() and removePhone() are utility methods that synchronize both ends whenever a child element is added or removed:

[Source - Hibernate User Guide](https://docs.jboss.org/hibernate/stable/orm/userguide/html_single/Hibernate_User_Guide.html#associations-one-to-many-bidirectional)

>However, we still need to have both sides in sync as otherwise, we break the Domain Model relationship consistency, and the entity state transitions are not guaranteed to work unless both sides are properly synchronized.

[Source - Vlad Mihalcea Blog](https://vladmihalcea.com/jpa-hibernate-synchronize-bidirectional-entity-associations/)

## Optional and nullability

Avoid nulls at all cost! Developer has to trust code otherwise you will find this pattern sprinkled ```Objects.requireNonNull``` or 
worse ```if (object != null) {}``` everywhere.
Defend at the perimeter! (Timestamped link, watch time ~5 min): [Clean Code: The Next Chapter by Victor Rentea](https://youtu.be/wY_CUkU1zfw?t=2800)  
All data which is incoming into the system from outside (REST request, RabbitMQ messages, Database entities etc.) has to follow 
these rules regarding nullability:
* If property can be null, you should use **Optional as a return parameter**.

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



https://stackoverflow.com/a/26328555/5486740

For example, you probably should never use it for something that returns an array of results, or a list of results; instead return an empty array or list. You should almost never use it as a field of something or a method parameter.

I think routinely using it as a return value for getters would definitely be over-use.

There's nothing wrong with Optional that it should be avoided, it's just not what many people wish it were, and accordingly we were fairly concerned about the risk of zealous over-use.

(Public service announcement: NEVER call Optional.get unless you can prove it will never be null; instead use one of the safe methods like orElse or ifPresent. In retrospect, we should have called get something like getOrElseThrowNoSuchElementException or something that made it far clearer that this was a highly dangerous method that undermined the whole purpose of Optional in the first place. Lesson learned. (UPDATE: Java 10 has Optional.orElseThrow(), which is semantically equivalent to get(), but whose name is more appropriate.))

### Records and nullability

https://mail.openjdk.java.net/pipermail/amber-dev/2020-March/005670.html

## Tests



### Naming

Follow test method naming conventions ``methodUnderTest__[given/when]__[then]`` where ``given/when`` and ``then`` is optional. 
Example: 
* ``getAllEmployees__thenReturnListOfEmployees``
* ``getAllEmployees__whenNoEmployeesExist__thenReturnEmptyList``
* ``getEmployee__whenInvalidEmployeeId__thenReturn400``
* ``getEmployee__whenEmployeeDoesNotExists__thenReturn404``

### Integration Tests

Reuse Spring's Application Context as much as possible. This will speed up your tests, because it doesn't need to restart, 
thus in perfect conditions, you'd only need to start Spring's Application once. To acheive this:
* Create a single annotation (see @IntegrationTest in test folder); 
* Class which each integration test extends.

---

Testing time.

TimeMachine class.

**Avoid @MockBeans.**

### Testing communication with other services

#### Wiremock

#### Contract Based Testing

#### End-to-End Testing

### Misc
```
assertThat(responseFrame).hasNoNullFieldsOrProperties();
```
If property wrapped into ``Optional``, it is not null, hence the assertion will pass.


### Resources

https://aerokhin.com/2020/09/28/what-is-the-right-unit-in-unit-test-after-all/

https://phauer.com/2019/modern-best-practices-testing-java/

## Spring

Favor construction injections vs setter/property injections. From Spring documentation:
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

---

Use UTF-8 when running surefire-plugin.

```
<!-- Some tests assert on UTF-8 letters, thus without defined encoding, tests will fail.
However they fail only when running with `mvn test`, but they succeed when launched through IntelliJ UI-->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <argLine>@{argLine} -Dfile.encoding=UTF-8</argLine>
    </configuration>
</plugin>
```

---

Don't forget to check whether surefire or jacoco supports Java version.

## HTTP Client

## No Async/await frameworks

ReactorX and friends. Project Loom/Virtual threads will cover 99% needs.

```
Thread.startVirtualThread(() -> {
    System.out.println("Hello, Loom!");
});
```

Key Takeaways:
* A virtual thread is a Thread — in code, at runtime, in the debugger and in the profiler.
* A virtual thread is not a wrapper around an OS thread, but a Java entity.
* Creating a virtual thread is cheap — have millions, and don’t pool them!
* Blocking a virtual thread is cheap — be synchronous!
* No language changes needed.
* Pluggable schedulers offer the flexibility of asynchronous programming.

Resources:
* [State of Loom](https://cr.openjdk.java.net/~rpressler/loom/loom/sol1_part1.html)
* [Taking Project Loom for a spin](https://renato.athaydes.com/posts/taking-loom-for-a-spin.html)
* [Project Loom - Modern Scalable Concurrency for the Java Platform](https://inside.java/2020/09/17/project-loom/)
* [On the Performance of User-Mode Threads and Coroutines](https://inside.java/2020/08/07/loom-performance/) 

## Application.yaml

Flyway configuration:
```
  flyway:
    enabled: true
    baseline-on-migrate: true
    ignore-missing-migrations: true #After some time you'll want to archive migrations scripts
    locations: classpath:/db/migration/h2
    installed-by: laurynas
```

Full ``application.yml``:
```
spring:
  main:
    banner-mode: off ##Turn off banner
  flyway:
    enabled: true
    baseline-on-migrate: true
    ignore-missing-migrations: true #After some time you'll want to archive migrations scripts
    locations: classpath:/db/migration/h2
    installed-by: laurynas
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:citizen;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
    platform: h2
  jpa:
    database: h2
    show-sql: true #Use ``true`` only when you need to debug. Otherwise performance will degraded due to stoud'ing all SQL opperations 
    generate-ddl: false #Never let Hibernate/JPA generate your database schemas. Otherwise, you might find surprises if you've annotated incorrectly. See unidirectional @OneToMany. 
    hibernate:
      ddl-auto: none #Do not autogenerate SQL tables. Sometimes it might lead to suboptimal structures/types. Control you database!
    properties:
      hibernate:
        #Turn it on only when you need for analysing queries. Do not leave it on!
        generate_statistics: false
        format_sql: true
        dialect: org.hibernate.dialect.H2Dialect
    open-in-view: false #Do not allow Spring to automagically fetch data from database. See https://vladmihalcea.com/the-open-session-in-view-anti-pattern/
  h2:
    console:
      enabled: true
      path: /h2
  application:
    name: good-citizen

server:
  port: 8080
  servlet:
    context-path: /
# Custom application properties.
application:
  version: v1
  base: /api/${application.version}
  endpoints:
    employees: ${application.base}/employees
    projects: ${application.base}/projects
    teams: ${application.base}/teams


management:
  endpoints:
    enabled-by-default: false # endpoint enablement to be opt-in rather than opt-out
    web:
      exposure:
        include: health, info, prometheus, metrics #exclude everything except health, info, prometheus endpoints
  endpoint:
    health:
      enabled: true
      show-details: always
    prometheus:
      enabled: true
    metrics:
      enabled: true
```

## Kubernetes pod template

## Database migration

## Git

What happens when tag is equal to branch name? It will checkout to tag, instead of branch. That means, that new code has been commited to branch and CI tool
would like to checkout to the head of named branch (using branch name), it will checkout to tag, and you won't have your changes deployed.

## Kotlin

https://github.com/jacoco/jacoco/issues/1086