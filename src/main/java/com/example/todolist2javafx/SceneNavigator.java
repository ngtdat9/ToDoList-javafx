package com.example.todolist2javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

final class SceneNavigator {
    private SceneNavigator() {
    }

    static void navigate(ActionEvent actionEvent, String fxmlFile, String title) {
        Parent root;
        try {
            root = FXMLLoader.load(SceneNavigator.class.getResource(fxmlFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}
