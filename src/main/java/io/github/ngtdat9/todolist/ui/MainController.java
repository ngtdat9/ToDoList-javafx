package io.github.ngtdat9.todolist.ui;

import io.github.ngtdat9.todolist.model.TodoData;
import io.github.ngtdat9.todolist.model.TodoItem;
import io.github.ngtdat9.todolist.storage.DataAccessException;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

public class MainController {
    private static final System.Logger LOG = System.getLogger(MainController.class.getName());
    private static final DateTimeFormatter DEADLINE_FORMAT = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    private static final PseudoClass DUE = PseudoClass.getPseudoClass("due");

    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadLineLabel;
    @FXML
    private BorderPane mainBorderPane;

    private final ContextMenu listContextMenu = new ContextMenu();

    @FXML
    private void initialize() {
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> withSelectedItem(this::deleteItem));
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(event -> withSelectedItem(this::showEditDialog));
        listContextMenu.getItems().addAll(deleteMenuItem, editMenuItem);

        todoListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showItemDetails(newValue));
        todoListView.setCellFactory(listView -> new TodoCell());
        todoListView.setItems(new SortedList<>(TodoData.getInstance().getTodoItems(),
                Comparator.comparing(TodoItem::getDeadline)));
        todoListView.getSelectionModel().selectFirst();
    }

    private void withSelectedItem(java.util.function.Consumer<TodoItem> action) {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            action.accept(item);
        }
    }

    private void showItemDetails(TodoItem item) {
        if (item == null) {
            itemDetailsTextArea.clear();
            deadLineLabel.setText("");
        } else {
            itemDetailsTextArea.setText(item.getDetails());
            deadLineLabel.setText(DEADLINE_FORMAT.format(item.getDeadline()));
        }
    }

    @FXML
    private void showNewDialog() {
        showTodoDialog("Add new", null).ifPresent(controller -> {
            try {
                todoListView.getSelectionModel().select(controller.createItem());
            } catch (DataAccessException e) {
                LOG.log(System.Logger.Level.ERROR, "Could not save the task", e);
                Alerts.showError("Could not save the task. Please try again.");
            }
        });
    }

    private void showEditDialog(TodoItem item) {
        showTodoDialog("Edit Task: " + item.getShortDescription(), item).ifPresent(controller -> {
            try {
                controller.updateItem(item);
                todoListView.refresh();
                todoListView.getSelectionModel().select(item);
                showItemDetails(item);
            } catch (DataAccessException e) {
                LOG.log(System.Logger.Level.ERROR, "Could not update the task", e);
                Alerts.showError("Could not update the task. Please try again.");
            }
        });
    }

    /** Returns the dialog's controller if the user confirmed valid input. */
    private Optional<TodoDialogController> showTodoDialog(String title, TodoItem itemToEdit) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("todo-item-dialog.fxml"));
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle(title);
        dialog.getDialogPane().getStylesheets().add(SceneNavigator.STYLESHEET);
        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            LOG.log(System.Logger.Level.ERROR, "Could not load the task dialog", e);
            Alerts.showError("Could not open the task dialog.");
            return Optional.empty();
        }
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TodoDialogController controller = loader.getController();
        if (itemToEdit != null) {
            controller.showItem(itemToEdit);
        }
        dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
            if (!controller.validate()) {
                event.consume();
            }
        });

        return dialog.showAndWait().filter(ButtonType.OK::equals).map(button -> controller);
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
            withSelectedItem(this::deleteItem);
        }
    }

    private void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to confirm or Cancel to back out.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                TodoData.getInstance().deleteTodoItem(item);
            } catch (DataAccessException e) {
                LOG.log(System.Logger.Level.ERROR, "Could not delete the task", e);
                Alerts.showError("Could not delete the task. Please try again.");
            }
        }
    }

    @FXML
    private void handleLogout() {
        TodoData.getInstance().logOut();
        SceneNavigator.showLogin((Stage) mainBorderPane.getScene().getWindow());
    }

    private class TodoCell extends ListCell<TodoItem> {
        TodoCell() {
            setOnContextMenuRequested(event -> {
                if (!isEmpty()) {
                    todoListView.getSelectionModel().select(getItem());
                }
            });
        }

        @Override
        protected void updateItem(TodoItem item, boolean empty) {
            super.updateItem(item, empty);
            // Cells are reused for other items, so every property must be set on every update.
            if (empty) {
                setText(null);
                pseudoClassStateChanged(DUE, false);
                setContextMenu(null);
            } else {
                setText(item.getShortDescription());
                pseudoClassStateChanged(DUE, item.getDeadline().isBefore(LocalDate.now().plusDays(1)));
                setContextMenu(listContextMenu);
            }
        }
    }
}
