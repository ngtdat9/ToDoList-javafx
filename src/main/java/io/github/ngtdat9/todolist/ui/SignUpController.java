package io.github.ngtdat9.todolist.ui;

import io.github.ngtdat9.todolist.auth.AuthService;
import io.github.ngtdat9.todolist.storage.DataAccessException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {
    private static final System.Logger LOG = System.getLogger(SignUpController.class.getName());

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    @FXML
    private void handleSignUp() {
        if (username.getText().isBlank() || password.getText().isBlank()) {
            Alerts.showError("Please fill in all the information");
            return;
        }
        try {
            if (AuthService.getInstance().signUp(username.getText(), password.getText()).isEmpty()) {
                Alerts.showError("This username is already taken.");
                return;
            }
            SceneNavigator.showLogin(stage());
        } catch (DataAccessException e) {
            LOG.log(System.Logger.Level.ERROR, "Could not sign up", e);
            Alerts.showError("Could not save to the database. Please try again.");
        }
    }

    @FXML
    private void showLogin() {
        SceneNavigator.showLogin(stage());
    }

    private Stage stage() {
        return (Stage) username.getScene().getWindow();
    }
}
