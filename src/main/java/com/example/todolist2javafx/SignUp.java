package com.example.todolist2javafx;

import com.example.todolist2javafx.auth.AuthService;
import com.example.todolist2javafx.storage.DataAccessException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUp implements Initializable {
    @FXML
    private Button signup_button;
    @FXML
    private Button log_in_button;
    @FXML
    private TextField username;
    @FXML
    private TextField password;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signup_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (username.getText().trim().isEmpty() || password.getText().trim().isEmpty()) {
                    Alerts.showError("Please fill in all the information");
                    return;
                }
                try {
                    if (AuthService.getInstance().signUp(username.getText(), password.getText()).isEmpty()) {
                        Alerts.showError("This username is already taken.");
                        return;
                    }
                    SceneNavigator.navigate(actionEvent, "LogIn.fxml", "Log In");
                } catch (DataAccessException e) {
                    e.printStackTrace();
                    Alerts.showError("Could not save to the database. Please try again.");
                }
            }
        });
        log_in_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                SceneNavigator.navigate(actionEvent, "LogIn.fxml", "Log In");
            }
        });
    }
}
