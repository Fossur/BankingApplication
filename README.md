# A small core banking solution

This is a small core bank solution test assignment.

## Technologies used:

- [x] Java 17
- [x] Spring Boot
- [x] MyBatis
- [x] Gradle
- [x] Postgres
- [x] RabbitMQ
- [x] JUnit
- [x] Liquibase

## Set-up

Running docker-compose from root directory sets up PostgreSQL and RabbitMQ.
Postgres is configured to run at port 5432, RabbitMQ at 5672 and its' UI at 15672 - these ports need to be free.

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

There is a Swagger OpenAPI specification in `spec/OpenAPI.yaml`.

You can create accounts with different balances (with different currencies) tied to it.
You can request that previous data.

Once an account has been created, you can make IN and OUT type of transactions with it.
The old balance data is invalidated to keep records.

You can also request all made transactions by an account;

All inserted and updated objects are send to a publishing service, which sends them to a configured exchange.
I've initially configured one DirectExchange to RabbitMQ with a dead-letter exchange for all messages,
but that can be quite easily switched out as needed.

The schema is the resources folder in `db/schema.sql`. Schema is set-up using liquibase migrations.

The code is itself structured by the given domain objects to limit access where no access is necessary.

Tests are done using `testcontainers` - it connects to your Docker and sets up a temporary PostgreSQL image
of your choice (in the test resources .properties file) - H2 might miss some errors that a real instance of
postgres might have. Tests have a high (90+%) coverage.


