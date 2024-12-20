package com.example.todolist2javafx.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;  
import java.time.format.DateTimeFormatter;
import java.sql.*;

public class TodoData {
    private static TodoData instance = new TodoData();
    private ObservableList<ToDoItem> toDoItems;
    private DateTimeFormatter formatter;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/todolist-javafx";
    private static final String DB_USER = "devuser";
    private static final String DB_PASSWORD = "Dpa0912@";

    public static TodoData getInstance() {
        return instance;
    }

    private TodoData() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        toDoItems = FXCollections.observableArrayList();
    }

    public ObservableList<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    public void addTodoItem(ToDoItem item) {
        toDoItems.add(item);
        saveTodoItemToDatabase(item);
    }

    public void loadTodoItems() {
        toDoItems.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            String query = "SELECT todo_id, short_description, details, deadline FROM todo_items";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int todoId = rs.getInt("todo_id");
                String shortDescription = rs.getString("short_description");
                String details = rs.getString("details");
                LocalDate deadline = rs.getDate("deadline").toLocalDate();

                ToDoItem toDoItem = new ToDoItem(shortDescription, details, deadline);
                toDoItem.setTodo_id(todoId);  // Set the todo_id for the task
                toDoItems.add(toDoItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void storeTodoItems() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String deleteQuery = "DELETE FROM todo_items";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(deleteQuery);
            }

            String insertQuery = "INSERT INTO todo_items (user_id, short_description, details, deadline) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                for (ToDoItem item : toDoItems) {
                    pstmt.setInt(1, 1); // Replace with actual user_id as needed
                    pstmt.setString(2, item.getShortDescription());
                    pstmt.setString(3, item.getDetails());
                    pstmt.setDate(4, Date.valueOf(item.getDeadline()));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteTodoItem(ToDoItem item) {
        toDoItems.remove(item);

        String deleteQuery = "DELETE FROM todo_items WHERE short_description = ? AND details = ? AND deadline = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setString(1, item.getShortDescription());
            pstmt.setString(2, item.getDetails());
            pstmt.setDate(3, Date.valueOf(item.getDeadline()));

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void saveTodoItemToDatabase(ToDoItem item) {
        String insertQuery = "INSERT INTO todo_items (user_id, short_description, details, deadline) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setInt(1, 1);
            pstmt.setString(2, item.getShortDescription());
            pstmt.setString(3, item.getDetails());
            pstmt.setDate(4, Date.valueOf(item.getDeadline()));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

