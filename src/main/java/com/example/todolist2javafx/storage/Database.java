package com.example.todolist2javafx.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    // Set -Dtodolist.db=/some/file.db to use a different database file.
    private static final String LOCATION_PROPERTY = "todolist.db";
    private static final Database instance = new Database(defaultLocation());

    private static final String CREATE_USERS = """
            CREATE TABLE IF NOT EXISTS users (
                user_id       INTEGER PRIMARY KEY,
                username      TEXT NOT NULL UNIQUE COLLATE NOCASE,
                password_hash TEXT NOT NULL
            )""";
    private static final String CREATE_TODO_ITEMS = """
            CREATE TABLE IF NOT EXISTS todo_items (
                todo_id           INTEGER PRIMARY KEY,
                user_id           INTEGER NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
                short_description TEXT NOT NULL,
                details           TEXT NOT NULL,
                deadline          TEXT NOT NULL
            )""";
    private static final String CREATE_TODO_ITEMS_INDEX =
            "CREATE INDEX IF NOT EXISTS todo_items_user_id ON todo_items (user_id)";

    private final Path location;

    public Database(Path location) {
        this.location = location;
    }

    public static Database getInstance() {
        return instance;
    }

    private static Path defaultLocation() {
        String configured = System.getProperty(LOCATION_PROPERTY);
        if (configured != null) {
            return Path.of(configured);
        }
        return Path.of(System.getProperty("user.home"), ".todolist-javafx", "todolist.db");
    }

    public void initialize() {
        try {
            Path parent = location.toAbsolutePath().getParent();
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw new DataAccessException("Could not create the database folder for " + location, e);
        }
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(CREATE_USERS);
            stmt.executeUpdate(CREATE_TODO_ITEMS);
            stmt.executeUpdate(CREATE_TODO_ITEMS_INDEX);
        } catch (SQLException e) {
            throw new DataAccessException("Could not create the database schema in " + location, e);
        }
    }

    Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + location);
        try (Statement stmt = conn.createStatement()) {
            // SQLite leaves foreign keys off unless each connection turns them on.
            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            conn.close();
            throw e;
        }
        return conn;
    }
}
