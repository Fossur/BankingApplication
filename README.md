# A small core banking solution

This is a small core bank solution test assignment.

## Technologies used:

- [x] Java 11+
- [x] SpringBoot
- [x] MyBatis
- [x] Gradle
- [x] Postgres
- [x] RabbitMQ
- [x] JUnit

## Requirements

* Java 17
* Docker

## Set-up

Running docker compose from root directory sets up PostgreSQL and RabbitMQ.
Postgres is configured to run at port 5432, RabbitMQ at 5672 and its' UI at 15672.

```
docker-compose up -d
```

## Running

Run Gradle in Bash with RabbitMQ and Postgres running.
```
./gradlew bootRun
```

Visual OpenAPI in the following URL with the default configuration

    localhost:8080/swagger-ui.html

## Tests

Running tests:
```
./gradlew check
```

# Q&A

## Explanation of important choices

I've built up four different API endpoints, two POSTs and two GETs.

There is a generated from Swagger OpenAPI specification in `spec/OpenAPI.yaml`.

You can create accounts with different balances (with different currencies) tied to it.
You can request that previous data.

Once an account has been created, you can make IN and OUT type of transactions with it.
The old balance data is invalidated to keep records.

You can also request all made transactions by an account;

All inserted and updated objects are send to a publishing service, which sends them to a configured exchange.
I've initially configured one DirectExchange to RabbitMQ with a dead-letter exchange for all messages,
but that can be quite easily switched out as needed. I understood that the Rabbit queue itself was for
other listeners only, but it can also be potentially used as a buffer for this service to help manage load.

The schema is the resources folder in `db/init.sql`.

The code is itself structured by the given domain objects to limit access where no access is necessary.

Tests are done using `testcontainers` - it connects to your Docker and sets up a temporary PostgreSQL image
of your choice (in the test resources .properties file) - H2 might miss some errors that a real instance of
postgres might have.

## Notes

Normally a database versioning system such as Flyway or Liquibase should be used as well, but the requirements
specified that the database needed to be initialized with the docker-compose file.

## Estimated TPS

A couple hundred requests per second on my development machine should be possible.

## Scaling it horizontally

Generally a matter of containerizing the application and using your container orchestration tool of choice (Kubernetes)
to add more pods/instances of the application. It makes sense to keep the services you want to scale stateless - use them
basically as functions only to make scaling easier.

