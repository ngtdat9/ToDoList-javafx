package io.github.ngtdat9.todolist.ui;

import io.github.ngtdat9.todolist.model.TodoData;
import io.github.ngtdat9.todolist.model.TodoItem;
import io.github.ngtdat9.todolist.model.TodoValidator;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Optional;

public class TodoDialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private Label errorLabel;

    /** Shows what is wrong with the input, if anything, and returns whether it can be saved. */
    boolean validate() {
        Optional<String> error = TodoValidator.validate(shortDescriptionField.getText(), deadlinePicker.getValue());
        errorLabel.setText(error.orElse(""));
        return error.isEmpty();
    }

    TodoItem createItem() {
        return TodoData.getInstance().addTodoItem(shortDescriptionField.getText().trim(),
                detailsArea.getText().trim(), deadlinePicker.getValue());
    }

    void showItem(TodoItem item) {
        shortDescriptionField.setText(item.getShortDescription());
        detailsArea.setText(item.getDetails());
        deadlinePicker.setValue(item.getDeadline());
    }

    void updateItem(TodoItem item) {
        TodoData.getInstance().updateTodoItem(item, shortDescriptionField.getText().trim(),
                detailsArea.getText().trim(), deadlinePicker.getValue());
    }
}
