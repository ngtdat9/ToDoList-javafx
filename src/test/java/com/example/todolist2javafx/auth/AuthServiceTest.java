package com.example.todolist2javafx.auth;

import com.example.todolist2javafx.datamodel.User;
import com.example.todolist2javafx.storage.Database;
import com.example.todolist2javafx.storage.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthService auth;

    @BeforeEach
    void setUp(@TempDir Path dir) {
        Database database = new Database(dir.resolve("test.db"));
        database.initialize();
        auth = new AuthService(new UserRepository(database));
    }

    @Test
    void logsInWithTheRightPassword() {
        User created = auth.signUp("dat", "secret").orElseThrow();

        assertEquals(Optional.of(created), auth.logIn("dat", "secret"));
    }

    @Test
    void rejectsAWrongPasswordAndAnUnknownUser() {
        auth.signUp("dat", "secret");

        assertTrue(auth.logIn("dat", "wrong").isEmpty());
        assertTrue(auth.logIn("nobody", "secret").isEmpty());
    }

    @Test
    void rejectsADuplicateUsernameIgnoringCaseAndSpaces() {
        auth.signUp("dat", "secret");

        assertTrue(auth.signUp("DAT", "other").isEmpty());
        assertTrue(auth.signUp("  dat ", "other").isEmpty());
        assertTrue(auth.logIn("dat", "other").isEmpty());
    }
}
