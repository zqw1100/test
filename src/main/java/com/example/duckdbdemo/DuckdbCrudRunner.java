package com.example.duckdbdemo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DuckdbCrudRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DuckdbCrudRunner.class);

    @Override
    public void run(String... args) throws Exception {
        Path dataDir = Path.of("data");
        Files.createDirectories(dataDir);
        String jdbcUrl = "jdbc:duckdb:" + dataDir.resolve("duckdb-demo.db");

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            createTable(connection);
            insertSample(connection);
            queryAll(connection);
            updateRecord(connection);
            deleteRecord(connection);
            queryAll(connection);
        }
    }

    private void createTable(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS people (id INTEGER PRIMARY KEY, name VARCHAR, age INTEGER)");
        }
        log.info("Table ensured.");
    }

    private void insertSample(Connection connection) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO people (id, name, age) VALUES (?, ?, ?)")) {
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, "Alice");
            preparedStatement.setInt(3, 30);
            preparedStatement.executeUpdate();

            preparedStatement.setInt(1, 2);
            preparedStatement.setString(2, "Bob");
            preparedStatement.setInt(3, 25);
            preparedStatement.executeUpdate();
        }
        log.info("Inserted sample rows.");
    }

    private void queryAll(Connection connection) throws Exception {
        log.info("Querying all rows:");
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, name, age FROM people ORDER BY id")) {
            while (resultSet.next()) {
                log.info("Row: id={}, name={}, age={}",
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"));
            }
        }
    }

    private void updateRecord(Connection connection) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE people SET age = ? WHERE name = ?")) {
            preparedStatement.setInt(1, 31);
            preparedStatement.setString(2, "Alice");
            int updated = preparedStatement.executeUpdate();
            log.info("Updated rows: {}", updated);
        }
    }

    private void deleteRecord(Connection connection) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM people WHERE name = ?")) {
            preparedStatement.setString(1, "Bob");
            int deleted = preparedStatement.executeUpdate();
            log.info("Deleted rows: {}", deleted);
        }
    }
}
