Goal

Demonstrate concepts and explain why choices of software engineering.

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

A more recent update on [record builders](https://github.com/openjdk/amber-docs/blob/master/eg-drafts/reconstruction-records-and-classes.md).
Possible syntax:
```
p with { x = 3; }
```

## Repository


## Tests


## Maven


## HTTP Client


## No Async/await frameworks

ReactorX and friends.

## Application.yaml


## Kubernetes pod template

## Database migration