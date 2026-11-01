package io.github.ngtdat9.todolist.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

public final class SceneNavigator {
    static final String STYLESHEET = SceneNavigator.class.getResource("app.css").toExternalForm();

    private SceneNavigator() {
    }

    public static void showLogin(Stage stage) {
        show(stage, "login.fxml", "To-Do List - Log In", 720, 480);
    }

    static void showSignUp(Stage stage) {
        show(stage, "signup.fxml", "To-Do List - Sign Up", 720, 480);
    }

    static void showMainWindow(Stage stage) {
        show(stage, "main-window.fxml", "To-Do List", 900, 600);
    }

    private static void show(Stage stage, String fxmlFile, String title, double width, double height) {
        Parent root;
        try {
            root = FXMLLoader.load(SceneNavigator.class.getResource(fxmlFile));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(STYLESHEET);
        stage.setTitle(title);
        stage.setMinWidth(600);
        stage.setMinHeight(440);
        stage.setScene(scene);
        stage.show();
    }
}
