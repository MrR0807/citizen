Goal

Demonstrate concepts and explain why choices of software engineering.

## Endpoints

1. Endpoints are mainly for documenting, logging and returning responses. They should contain as little business logic as possible. All the business logic should be delegated to Service Layer.
2. Use ``@ApiOperation`` to describe an endpoint's purpose. There is no need to describe ``response`` or ``responseContainer`` as it is inferred by SpringFox automatically.
3. Request models should preferably be:
* instance variables ``private final``;
* Have ``all-args`` constructor with ``@JsonCreator``;
* hashCode, equals, toString;