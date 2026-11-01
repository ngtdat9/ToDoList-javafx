package com.example.todolist2javafx;

import com.example.todolist2javafx.datamodel.ToDoItem;
import com.example.todolist2javafx.datamodel.TodoData;
import com.example.todolist2javafx.storage.DataAccessException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HelloController {
    @FXML
    private Button logout_button;
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
    
    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem editMenuItem = new MenuItem("Edit");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    deleteItem(item);
                }
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
                showItemDetails(newValue);
            }
        });

        SortedList<ToDoItem> sortedList = new SortedList<ToDoItem>(TodoData.getInstance().getToDoItems(),
                new Comparator<ToDoItem>() {
                    @Override
                    public int compare(ToDoItem o1, ToDoItem o2) {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });

        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<ToDoItem>() {
                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        // Cells are reused for other items, so every property must be set on every update.
                        if (empty) {
                            setText(null);
                            setStyle("");
                            setContextMenu(null);
                        } else {
                            setText(item.getShortDescription());
                            boolean dueNow = item.getDeadline().isBefore(LocalDate.now().plusDays(1));
                            setStyle(dueNow ? "-fx-text-fill: red;" : "");
                            setContextMenu(listContextMenu);
                        }
                    }
                };

                cell.setOnContextMenuRequested(event -> {
                    if (!cell.isEmpty()) {
                        todoListView.getSelectionModel().select(cell.getItem());
                    }
                });
                return cell;
            }
        });

    }

    private void showItemDetails(ToDoItem item) {
        if (item == null) {
            itemDetailsTextArea.clear();
            deadLineLabel.setText("");
        } else {
            itemDetailsTextArea.setText(item.getDetails());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            deadLineLabel.setText(dtf.format(item.getDeadline()));
        }
    }

    private void keepOpenUntilValid(Dialog<ButtonType> dialog, DialogController controller) {
        dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
            if (!controller.validate()) {
                event.consume();
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
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        DialogController controller = fxmlLoader.getController();
        keepOpenUntilValid(dialog, controller);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ToDoItem newItem = controller.processResults();
                todoListView.getSelectionModel().select(newItem);
            } catch (DataAccessException e) {
                e.printStackTrace();
                Alerts.showError("Could not save the task. Please try again.");
            }
        }
    }

    @FXML
    public void showEditItemDialog(ToDoItem selectedItem) {
        Dialog<ButtonType> editDialog = new Dialog<>();
        editDialog.initOwner(mainBorderPane.getScene().getWindow());
        editDialog.setTitle("Edit Task: " + selectedItem.getShortDescription());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));

        try {
            editDialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        editDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        editDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        DialogController editDialogController = fxmlLoader.getController();
        editDialogController.adoptItemDetails(selectedItem); // Pre-fill dialog fields with current task details
        keepOpenUntilValid(editDialog, editDialogController);

        Optional<ButtonType> result = editDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                editDialogController.modifyItemDetails(selectedItem);
                todoListView.refresh();
                todoListView.getSelectionModel().select(selectedItem);
                showItemDetails(selectedItem);
            } catch (DataAccessException e) {
                e.printStackTrace();
                Alerts.showError("Could not update the task. Please try again.");
            }
        }
    }




    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        KeyCode code = keyEvent.getCode();
        if (selectedItem != null && (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE)) {
            deleteItem(selectedItem);
        }
    }

    public void deleteItem (ToDoItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item:"+ item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to confirm or cancel to Back out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)){
            try {
                TodoData.getInstance().deleteTodoItem(item);
            } catch (DataAccessException e) {
                e.printStackTrace();
                Alerts.showError("Could not delete the task. Please try again.");
            }
        }
    }

    @FXML
    private void handleLogout() {
        TodoData.getInstance().logOut();
        try {
            Stage currentStage = (Stage) (logout_button.getScene().getWindow());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LogIn.fxml"));
            Parent root = loader.load();
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}