package io.github.ngtdat9.todolist.model;

import io.github.ngtdat9.todolist.storage.Database;
import io.github.ngtdat9.todolist.storage.TodoRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

/** The logged-in user's tasks. Each change is saved first, so a failed save leaves the list as it was. */
public class TodoData {
    private static final TodoData instance = new TodoData(new TodoRepository(Database.getInstance()));

    private final TodoRepository repository;
    private final ObservableList<TodoItem> todoItems;
    private User currentUser;

    TodoData(TodoRepository repository) {
        this.repository = repository;
        todoItems = FXCollections.observableArrayList();
    }

    public static TodoData getInstance() {
        return instance;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void logIn(User user) {
        todoItems.setAll(repository.findByUser(user.id()));
        currentUser = user;
    }

    public void logOut() {
        currentUser = null;
        todoItems.clear();
    }

    public TodoItem addTodoItem(String shortDescription, String details, LocalDate deadline) {
        TodoItem item = new TodoItem(shortDescription, details, deadline);
        item.setId(repository.insert(currentUser.id(), shortDescription, details, deadline));
        todoItems.add(item);
        return item;
    }

    public void updateTodoItem(TodoItem item, String shortDescription, String details, LocalDate deadline) {
        repository.update(currentUser.id(), item.getId(), shortDescription, details, deadline);
        item.setShortDescription(shortDescription);
        item.setDetails(details);
        item.setDeadline(deadline);
        // Replacing the element tells list views and sorted lists that the item changed.
        todoItems.set(todoItems.indexOf(item), item);
    }

    public void deleteTodoItem(TodoItem item) {
        repository.delete(currentUser.id(), item.getId());
        todoItems.remove(item);
    }
}
