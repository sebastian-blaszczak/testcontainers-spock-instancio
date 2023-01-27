# Testcontainers Spock Instancio

Simple Spring Boot project that combines 3 powerful test tools: Spock, Testcontainers and Instancio.

## How to run

First step is to run `docker-compose.yml`, this will spin elastic search container

```
docker-compose up
```

In second step You need to start up Spring Boot app

```
mvn spring-boot:run
```

Last step is optional, You can either import collection to Postman or use `curl` to communicate through API
Postman collection is located in `project.postman_collection.json`