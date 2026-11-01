package com.example.todolist2javafx;


import com.example.todolist2javafx.auth.AuthService;
import com.example.todolist2javafx.datamodel.TodoData;
import com.example.todolist2javafx.datamodel.User;
import com.example.todolist2javafx.storage.DataAccessException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LogIn implements Initializable {
    @FXML
    private Button login_button;
    @FXML
    private Button sign_up_button;
    @FXML
    private TextField username;
    @FXML
    private TextField password;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    Optional<User> user = AuthService.getInstance().logIn(username.getText(), password.getText());
                    if (user.isEmpty()) {
                        Alerts.showError("Wrong username or password.");
                        return;
                    }
                    TodoData.getInstance().logIn(user.get());
                    SceneNavigator.navigate(actionEvent, "main-window.fxml", "Main Window");
                } catch (DataAccessException e) {
                    e.printStackTrace();
                    Alerts.showError("Could not read the database. Please try again.");
                }
            }
        });
        sign_up_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                SceneNavigator.navigate(actionEvent, "Signup.fxml", "Sign Up");
            }
        });
    }
}
