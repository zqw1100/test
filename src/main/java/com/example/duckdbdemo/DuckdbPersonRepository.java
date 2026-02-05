package com.example.duckdbdemo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DuckdbPersonRepository {

    private final String jdbcUrl;

    public DuckdbPersonRepository() throws Exception {
        Path dataDir = Path.of("data");
        Files.createDirectories(dataDir);
        this.jdbcUrl = "jdbc:duckdb:" + dataDir.resolve("duckdb-demo.db");
    }

    public List<Person> findAll() throws Exception {
        List<Person> people = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, name, age FROM people ORDER BY id")) {
            while (resultSet.next()) {
                people.add(new Person(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age")));
            }
        }
        return people;
    }

    public Person findById(long id) throws Exception {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, name, age FROM people WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Person(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getInt("age"));
                }
            }
        }
        return null;
    }

    public Person create(PersonRequest request) throws Exception {
        long nextId = nextId();
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO people (id, name, age) VALUES (?, ?, ?)")) {
            statement.setLong(1, nextId);
            statement.setString(2, request.name());
            statement.setInt(3, request.age());
            statement.executeUpdate();
        }
        return new Person(nextId, request.name(), request.age());
    }

    public Person update(long id, PersonRequest request) throws Exception {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE people SET name = ?, age = ? WHERE id = ?")) {
            statement.setString(1, request.name());
            statement.setInt(2, request.age());
            statement.setLong(3, id);
            int updated = statement.executeUpdate();
            if (updated == 0) {
                return null;
            }
        }
        return new Person(id, request.name(), request.age());
    }

    public boolean delete(long id) throws Exception {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM people WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private long nextId() throws Exception {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COALESCE(MAX(id), 0) + 1 AS next_id FROM people")) {
            if (resultSet.next()) {
                return resultSet.getLong("next_id");
            }
        }
        return 1L;
    }
}
