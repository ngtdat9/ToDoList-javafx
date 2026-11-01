package com.example.todolist2javafx;

import javafx.scene.control.Alert;

final class Alerts {
    private Alerts() {
    }

    static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}
