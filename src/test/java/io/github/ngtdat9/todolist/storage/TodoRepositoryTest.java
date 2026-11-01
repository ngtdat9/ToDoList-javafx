package io.github.ngtdat9.todolist.storage;

import io.github.ngtdat9.todolist.model.TodoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TodoRepositoryTest {
    private static final LocalDate DEADLINE = LocalDate.of(2027, 1, 15);

    private Path location;
    private TodoRepository todos;
    private int alice;
    private int bob;

    @BeforeEach
    void setUp(@TempDir Path dir) {
        location = dir.resolve("nested/test.db");
        Database database = new Database(location);
        database.initialize();
        todos = new TodoRepository(database);
        UserRepository users = new UserRepository(database);
        alice = users.create("alice", "hash").orElseThrow().id();
        bob = users.create("bob", "hash").orElseThrow().id();
    }

    @Test
    void insertedItemIsReturnedWithAllItsFields() {
        int id = todos.insert(alice, "Title", "Details", DEADLINE);

        TodoItem item = todos.findByUser(alice).getFirst();
        assertEquals(id, item.getId());
        assertEquals("Title", item.getShortDescription());
        assertEquals("Details", item.getDetails());
        assertEquals(DEADLINE, item.getDeadline());
    }

    @Test
    void dataSurvivesReopeningTheDatabase() {
        todos.insert(alice, "Title", "Details", DEADLINE);

        Database reopened = new Database(location);
        reopened.initialize();

        assertEquals(1, new TodoRepository(reopened).findByUser(alice).size());
    }

    @Test
    void usersOnlySeeTheirOwnItems() {
        todos.insert(alice, "Alice's", "", DEADLINE);
        todos.insert(bob, "Bob's", "", DEADLINE);

        List<TodoItem> items = todos.findByUser(alice);

        assertEquals(1, items.size());
        assertEquals("Alice's", items.getFirst().getShortDescription());
    }

    @Test
    void usersCannotChangeOrDeleteAnotherUsersItem() {
        int id = todos.insert(alice, "Alice's", "", DEADLINE);

        todos.update(bob, id, "Changed", "", DEADLINE);
        todos.delete(bob, id);

        assertEquals("Alice's", todos.findByUser(alice).getFirst().getShortDescription());
    }

    @Test
    void updateAndDeleteOnlyTouchTheTargetItem() {
        int first = todos.insert(alice, "Same", "Same", DEADLINE);
        int second = todos.insert(alice, "Same", "Same", DEADLINE);

        todos.update(alice, first, "Changed", "Same", DEADLINE);
        assertEquals("Changed", todos.findByUser(alice).getFirst().getShortDescription());

        todos.delete(alice, first);
        List<TodoItem> remaining = todos.findByUser(alice);
        assertEquals(1, remaining.size());
        assertEquals(second, remaining.getFirst().getId());
    }

    @Test
    void anItemWithoutADeadlineIsRejected() {
        assertThrows(DataAccessException.class, () -> todos.insert(alice, "Title", "", null));
        assertTrue(todos.findByUser(alice).isEmpty());
    }

    @Test
    void anItemForAnUnknownUserIsRejected() {
        assertThrows(DataAccessException.class, () -> todos.insert(9999, "Title", "", DEADLINE));
    }
}
