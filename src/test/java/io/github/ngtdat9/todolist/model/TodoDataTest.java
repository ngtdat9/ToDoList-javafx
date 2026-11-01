package io.github.ngtdat9.todolist.model;

import io.github.ngtdat9.todolist.storage.DataAccessException;
import io.github.ngtdat9.todolist.storage.Database;
import io.github.ngtdat9.todolist.storage.TodoRepository;
import io.github.ngtdat9.todolist.storage.UserRepository;
import javafx.collections.transformation.SortedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class TodoDataTest {
    private static final LocalDate DEADLINE = LocalDate.of(2027, 1, 15);

    private TodoData data;
    private User alice;
    private User bob;

    @BeforeEach
    void setUp(@TempDir Path dir) {
        Database database = new Database(dir.resolve("test.db"));
        database.initialize();
        data = new TodoData(new TodoRepository(database));
        UserRepository users = new UserRepository(database);
        alice = users.create("alice", "hash").orElseThrow();
        bob = users.create("bob", "hash").orElseThrow();
    }

    @Test
    void changesAreStillThereAfterLoggingBackIn() {
        data.logIn(alice);
        TodoItem kept = data.addTodoItem("Keep", "", DEADLINE);
        TodoItem removed = data.addTodoItem("Remove", "", DEADLINE);
        data.updateTodoItem(kept, "Kept", "Edited", DEADLINE.plusDays(1));
        data.deleteTodoItem(removed);

        data.logOut();
        assertTrue(data.getTodoItems().isEmpty());
        data.logIn(alice);

        assertEquals(1, data.getTodoItems().size());
        TodoItem reloaded = data.getTodoItems().getFirst();
        assertEquals("Kept", reloaded.getShortDescription());
        assertEquals("Edited", reloaded.getDetails());
        assertEquals(DEADLINE.plusDays(1), reloaded.getDeadline());
    }

    @Test
    void changingADeadlineMovesTheItemInASortedList() {
        data.logIn(alice);
        TodoItem first = data.addTodoItem("First", "", DEADLINE);
        TodoItem second = data.addTodoItem("Second", "", DEADLINE.plusDays(1));
        SortedList<TodoItem> sorted = new SortedList<>(data.getTodoItems(), Comparator.comparing(TodoItem::getDeadline));
        assertEquals(first, sorted.getFirst());

        data.updateTodoItem(first, "First", "", DEADLINE.plusDays(2));

        assertEquals(second, sorted.getFirst());
    }

    @Test
    void switchingUsersSwitchesTheList() {
        data.logIn(alice);
        data.addTodoItem("Alice's", "", DEADLINE);
        data.logOut();

        data.logIn(bob);

        assertTrue(data.getTodoItems().isEmpty());
    }

    @Test
    void aFailedSaveLeavesTheListAndItemUnchanged() {
        data.logIn(alice);
        TodoItem item = data.addTodoItem("Title", "", DEADLINE);

        assertThrows(DataAccessException.class, () -> data.addTodoItem("No deadline", "", null));
        assertThrows(DataAccessException.class, () -> data.updateTodoItem(item, "Changed", "", null));

        assertEquals(1, data.getTodoItems().size());
        assertEquals("Title", item.getShortDescription());
        assertEquals(DEADLINE, item.getDeadline());
    }
}
