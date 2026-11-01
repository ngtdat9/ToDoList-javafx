package com.example.todolist2javafx.storage;

import com.example.todolist2javafx.datamodel.ToDoItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Every query is scoped by user id, so one user can never read or change another user's tasks. */
public class TodoRepository {
    private final Database database;

    public TodoRepository(Database database) {
        this.database = database;
    }

    public List<ToDoItem> findByUser(int userId) {
        String query = "SELECT todo_id, short_description, details, deadline FROM todo_items WHERE user_id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            List<ToDoItem> items = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ToDoItem item = new ToDoItem(rs.getString("short_description"), rs.getString("details"),
                            LocalDate.parse(rs.getString("deadline")));
                    item.setId(rs.getInt("todo_id"));
                    items.add(item);
                }
            }
            return items;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load the tasks", e);
        }
    }

    /** Stores the item and returns its generated id. */
    public int insert(int userId, String shortDescription, String details, LocalDate deadline) {
        String insertQuery = "INSERT INTO todo_items (user_id, short_description, details, deadline) "
                + "VALUES (?, ?, ?, ?) RETURNING todo_id";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, shortDescription);
            pstmt.setString(3, details);
            pstmt.setString(4, deadline == null ? null : deadline.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt("todo_id");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not save the task", e);
        }
    }

    public void update(int userId, int todoId, String shortDescription, String details, LocalDate deadline) {
        String updateQuery = "UPDATE todo_items SET short_description = ?, details = ?, deadline = ? "
                + "WHERE todo_id = ? AND user_id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setString(1, shortDescription);
            pstmt.setString(2, details);
            pstmt.setString(3, deadline == null ? null : deadline.toString());
            pstmt.setInt(4, todoId);
            pstmt.setInt(5, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update the task", e);
        }
    }

    public void delete(int userId, int todoId) {
        String deleteQuery = "DELETE FROM todo_items WHERE todo_id = ? AND user_id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, todoId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete the task", e);
        }
    }
}
