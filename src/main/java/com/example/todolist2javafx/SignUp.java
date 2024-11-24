package com.example.todolist2javafx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
                if (!username.getText().trim().isEmpty() && !password.getText().trim().isEmpty()) {
                    DBmanage.signUp(actionEvent, username.getText(), password.getText());
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please fill in all the information");
                    alert.show();

                }
            }
        });
        log_in_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                DBmanage.navigate(actionEvent, "LogIn.fxml","Log In",null);
            }
        });
    }
}

