package com.example.todolist2javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBmanage {

    public static void navigate(ActionEvent action_event, String fxmlFile, String tile, String username) {
        Parent root = null;

        if (username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBmanage.class.getResource(fxmlFile));
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                root = FXMLLoader.load(DBmanage.class.getResource(fxmlFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Stage stage = (Stage) ((Node) action_event.getSource()).getScene().getWindow();
        stage.setTitle(tile);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public static void signUp(ActionEvent actionEvent, String username, String password) {
        String url = "jdbc:mysql://127.0.0.1:3306/todolist-javafx";
        String dbUser = "devuser";
        String dbPassword = "Dpa0912@";

        try (
                Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
                PreparedStatement checkExist = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
                PreparedStatement psInsert = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")
        ) {
            checkExist.setString(1, username);
            try (ResultSet resultSet = checkExist.executeQuery()) {
                if (resultSet.isBeforeFirst()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("This username is already taken.");
                    alert.show();
                    return;
                }
            }

            psInsert.setString(1, username);
            psInsert.setString(2, password);
            psInsert.executeUpdate();

            navigate(actionEvent, "LogIn.fxml", "Log In", null);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("An error occurred while connecting to the database. Please try again.");
            alert.show();
        }
    }

    public static void LogIn(ActionEvent actionEvent, String username, String password){
        String url = "jdbc:mysql://127.0.0.1:3306/todolist-javafx";
        String dbUser = "devuser";
        String dbPassword = "Dpa0912@";
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            conn = DriverManager.getConnection(url,dbUser,dbPassword);
            preparedStatement = conn.prepareStatement("SELECT password FROM users WHERE username = ?");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                System.out.println("Username not found in the db");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Wrong credentials");
                alert.show();
            } else {
                while (resultSet.next()) {
                    String retrievedPass = resultSet.getString("password");
                    if (retrievedPass.equals(password)) {
                        navigate(actionEvent,"main-window.fxml","Main Window", username);
//                        TodoData.getInstance().loadTodoItems(username);
                    } else {
                        System.out.println("Wrong password");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Wrong password. Try again");
                        alert.show();
                    }
                }

            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
}

