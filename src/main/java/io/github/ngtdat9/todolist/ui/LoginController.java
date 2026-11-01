package io.github.ngtdat9.todolist.ui;

import io.github.ngtdat9.todolist.auth.AuthService;
import io.github.ngtdat9.todolist.model.TodoData;
import io.github.ngtdat9.todolist.model.User;
import io.github.ngtdat9.todolist.storage.DataAccessException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {
    private static final System.Logger LOG = System.getLogger(LoginController.class.getName());

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    @FXML
    private void handleLogin() {
        try {
            Optional<User> user = AuthService.getInstance().logIn(username.getText(), password.getText());
            if (user.isEmpty()) {
                Alerts.showError("Wrong username or password.");
                return;
            }
            TodoData.getInstance().logIn(user.get());
            SceneNavigator.showMainWindow(stage());
        } catch (DataAccessException e) {
            LOG.log(System.Logger.Level.ERROR, "Could not log in", e);
            Alerts.showError("Could not read the database. Please try again.");
        }
    }

    @FXML
    private void showSignUp() {
        SceneNavigator.showSignUp(stage());
    }

    private Stage stage() {
        return (Stage) username.getScene().getWindow();
    }
}
