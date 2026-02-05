# DuckDB Spring Boot Demo

This project is a minimal Spring Boot application that connects to a local DuckDB file and exposes CRUD APIs for testing.

## Requirements

- Java 17+
- Maven 3.9+

## Run

```bash
mvn spring-boot:run
```

The app will create a `data/duckdb-demo.db` file and initialize the `people` table.

## API examples

```bash
# Create
curl -X POST http://localhost:8080/api/people \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","age":30}'

# List
curl http://localhost:8080/api/people

# Get by id
curl http://localhost:8080/api/people/1

# Update
curl -X PUT http://localhost:8080/api/people/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","age":31}'

# Delete
curl -X DELETE http://localhost:8080/api/people/1
```
