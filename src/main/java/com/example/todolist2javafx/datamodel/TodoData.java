package com.example.todolist2javafx.datamodel;

import com.example.todolist2javafx.storage.Database;
import com.example.todolist2javafx.storage.TodoRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

/** The logged-in user's tasks. Each change is saved first, so a failed save leaves the list as it was. */
public class TodoData {
    private static final TodoData instance = new TodoData(new TodoRepository(Database.getInstance()));

    private final TodoRepository repository;
    private final ObservableList<ToDoItem> toDoItems;
    private User currentUser;

    TodoData(TodoRepository repository) {
        this.repository = repository;
        toDoItems = FXCollections.observableArrayList();
    }

    public static TodoData getInstance() {
        return instance;
    }

    public ObservableList<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    public void logIn(User user) {
        toDoItems.setAll(repository.findByUser(user.id()));
        currentUser = user;
    }

    public void logOut() {
        currentUser = null;
        toDoItems.clear();
    }

    public ToDoItem addTodoItem(String shortDescription, String details, LocalDate deadline) {
        ToDoItem item = new ToDoItem(shortDescription, details, deadline);
        item.setId(repository.insert(currentUser.id(), shortDescription, details, deadline));
        toDoItems.add(item);
        return item;
    }

    public void updateTodoItem(ToDoItem item, String shortDescription, String details, LocalDate deadline) {
        repository.update(currentUser.id(), item.getId(), shortDescription, details, deadline);
        item.setShortDescription(shortDescription);
        item.setDetails(details);
        item.setDeadline(deadline);
        // Replacing the element tells list views and sorted lists that the item changed.
        toDoItems.set(toDoItems.indexOf(item), item);
    }

    public void deleteTodoItem(ToDoItem item) {
        repository.delete(currentUser.id(), item.getId());
        toDoItems.remove(item);
    }
}
