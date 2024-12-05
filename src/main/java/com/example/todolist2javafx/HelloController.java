package com.example.todolist2javafx;

import com.example.todolist2javafx.datamodel.ToDoItem;
import com.example.todolist2javafx.datamodel.TodoData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class HelloController {
    @FXML
    private ListView<ToDoItem> todoListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadLineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;

    private List<ToDoItem> todoItems;
    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem editMenuItem = new MenuItem("Edit");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });
        listContextMenu.getItems().add(deleteMenuItem);

        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    showEditItemDialog(item);
                }
            }
        });
        listContextMenu.getItems().add(editMenuItem);

        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue, ToDoItem oldValue, ToDoItem newValue) {
                if (newValue != null) {
                    ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    deadLineLabel.setText(dtf.format(item.getDeadline()));
                }
            }
        });

        todoListView.setItems(TodoData.getInstance().getToDoItems());
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<ToDoItem>() {
                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getShortDescription());
                            if (item.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            }
                        }
                    }
                };

                cell.setContextMenu(listContextMenu);
                cell.setOnContextMenuRequested(event -> {
                    if (!cell.isEmpty()) {
                        todoListView.getSelectionModel().select(cell.getItem());
                        listContextMenu.show(cell, event.getScreenX(), event.getScreenY());
                    }
                });
                return cell;
            }
        });
    }
    @FXML
    public void showNewDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Could not load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            ToDoItem newItem = controller.processResults();
//            todoListView.getItems().setAll(TodoData.getInstance().getToDoItems());
            todoListView.getSelectionModel().select(newItem);
            System.out.println("User clicked OK");
        } else {
            System.out.println("User canceled");
        }
    }

    @FXML
    public void showEditItemDialog(ToDoItem selectedItem) {
        if (selectedItem == null) {
            System.out.println("No item selected to edit.");
            return;
        }

        Dialog<ButtonType> editDialog = new Dialog<>();
        editDialog.initOwner(mainBorderPane.getScene().getWindow());
        editDialog.setTitle("Edit Task: " + selectedItem.getShortDescription());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));

        try {
            editDialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("The dialog could not be loaded");
            e.printStackTrace();
            return;
        }

        editDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        editDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        DialogController editDialogController = fxmlLoader.getController();
        editDialogController.adoptItemDetails(selectedItem); // Nạp thông tin cũ vào dialog

        Optional<ButtonType> result = editDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            editDialogController.modifyItemDetails(selectedItem); // Lấy thông tin mới
            TodoData.getInstance().storeTodoItems(); // Lưu lại vào cơ sở dữ liệu
            todoListView.refresh(); // Làm mới danh sách hiển thị
        }
    }


    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        System.out.println("Key pressed: " + keyEvent.getCode());
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            System.out.println("No item is selected.");
        } else {
            System.out.println("Selected item: " + selectedItem.getShortDescription());
            if (keyEvent.getCode().equals(KeyCode.BACK_SPACE)) {
                deleteItem(selectedItem);
            }
        }
    }

    public void deleteItem (ToDoItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item:"+ item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to confirm or cancel to Back out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)){
            TodoData.getInstance().deleteTodoItem(item);
        }
    }
}