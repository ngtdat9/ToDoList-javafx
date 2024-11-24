package com.example.todolist2javafx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenu implements Initializable {
    @FXML
    private Button logout_button;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logout_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBmanage.navigate(actionEvent, "logIn.fxml","Log In!",null);
            }
        });
    }
}

