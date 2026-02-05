# DuckDB Spring Boot Demo

This project is a minimal Spring Boot application that connects to a local DuckDB file and demonstrates basic CRUD operations on startup.

## Requirements

- Java 17+
- Maven 3.9+

## Run

```bash
mvn spring-boot:run
```

The app will create a `data/duckdb-demo.db` file and log CRUD operations for a `people` table.
