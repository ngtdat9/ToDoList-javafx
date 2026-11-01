package com.example.todolist2javafx;

import com.example.todolist2javafx.storage.DataAccessException;
import com.example.todolist2javafx.storage.Database;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            Database.getInstance().initialize();
        } catch (DataAccessException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not open the database: " + e.getMessage()).showAndWait();
            Platform.exit();
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("ToDoList");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
