package io.github.ngtdat9.todolist.storage;

import io.github.ngtdat9.todolist.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository {
    public record StoredCredentials(User user, String passwordHash) {
    }

    private final Database database;

    public UserRepository(Database database) {
        this.database = database;
    }

    /** Returns the new user, or empty if the username is already taken. */
    public Optional<User> create(String username, String passwordHash) {
        String insertQuery = "INSERT INTO users (username, password_hash) VALUES (?, ?) "
                + "ON CONFLICT (username) DO NOTHING RETURNING user_id";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(new User(rs.getInt("user_id"), username)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not create the user", e);
        }
    }

    public Optional<StoredCredentials> findCredentials(String username) {
        String query = "SELECT user_id, username, password_hash FROM users WHERE username = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                User user = new User(rs.getInt("user_id"), rs.getString("username"));
                return Optional.of(new StoredCredentials(user, rs.getString("password_hash")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not read the user", e);
        }
    }
}
