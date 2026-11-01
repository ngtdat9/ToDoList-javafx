package com.example.todolist2javafx;

import com.example.todolist2javafx.datamodel.ToDoItem;
import com.example.todolist2javafx.datamodel.TodoData;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;

    public ToDoItem processResults(){
        return TodoData.getInstance().addTodoItem(shortDescriptionField.getText().trim(),
                detailsArea.getText().trim(), deadlinePicker.getValue());
    }
    public void adoptItemDetails(ToDoItem edited) {
        shortDescriptionField.setText(edited.getShortDescription());
        detailsArea.setText(edited.getDetails());
        deadlinePicker.setValue(edited.getDeadline());
    }
    public void modifyItemDetails(ToDoItem item) {
        TodoData.getInstance().updateTodoItem(item, shortDescriptionField.getText().trim(),
                detailsArea.getText().trim(), deadlinePicker.getValue());
    }
}


