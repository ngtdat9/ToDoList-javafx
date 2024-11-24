package com.example.todolist2javafx;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
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
                DBmanage.LogIn(actionEvent, username.getText(), password.getText());
            }
        });
        sign_up_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBmanage.navigate(actionEvent, "Signup.fxml","Sign Up",null);
            }
        });
    }
}

