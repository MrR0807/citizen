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

## Repository

Use ``getResultList()`` vs ``getSingleResult()``, because ``getSingleResult()`` throws ``NoResultException`` - if there is no result, thus you would have to
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


### Entities

``equals`` and ``hashCode`` is implemtend according to Vlad Mihalcea. More information can be found in *High-Performance Java Persistence* or in this [link](https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/).

If entity can return null value, wrap *getter* into ``Optional``.

``Entities`` should not leave Service layer. Always return a *view* of ``Entity`` instead. 

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