package com.example.duckdbdemo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DuckdbInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DuckdbInitializer.class);

    @Override
    public void run(String... args) throws Exception {
        Path dataDir = Path.of("data");
        Files.createDirectories(dataDir);
        String jdbcUrl = "jdbc:duckdb:" + dataDir.resolve("duckdb-demo.db");

        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS people (" +
                            "id BIGINT PRIMARY KEY, " +
                            "name VARCHAR, " +
                            "age INTEGER"
                            + ")");
        }

        log.info("DuckDB initialized at {}", jdbcUrl);
    }
}
