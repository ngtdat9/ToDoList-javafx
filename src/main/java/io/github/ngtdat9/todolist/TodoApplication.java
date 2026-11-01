package io.github.ngtdat9.todolist;

import io.github.ngtdat9.todolist.storage.DataAccessException;
import io.github.ngtdat9.todolist.storage.Database;
import io.github.ngtdat9.todolist.ui.SceneNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TodoApplication extends Application {
    private static final System.Logger LOG = System.getLogger(TodoApplication.class.getName());

    @Override
    public void start(Stage stage) {
        try {
            Database.getInstance().initialize();
        } catch (DataAccessException e) {
            LOG.log(System.Logger.Level.ERROR, "Could not open the database", e);
            new Alert(Alert.AlertType.ERROR, "Could not open the database: " + e.getMessage()).showAndWait();
            Platform.exit();
            return;
        }

        stage.getIcons().add(new Image(TodoApplication.class.getResource("ui/images/logo.png").toExternalForm()));
        SceneNavigator.showLogin(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
