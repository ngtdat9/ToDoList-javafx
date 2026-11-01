package io.github.ngtdat9.todolist.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    @Test
    void verifiesTheCorrectPasswordOnly() {
        String stored = PasswordHasher.hash("correct horse");

        assertTrue(PasswordHasher.verify("correct horse", stored));
        assertFalse(PasswordHasher.verify("Correct horse", stored));
    }

    @Test
    void neverStoresThePasswordAndSaltsEachHash() {
        String first = PasswordHasher.hash("correct horse");
        String second = PasswordHasher.hash("correct horse");

        assertFalse(first.contains("correct horse"));
        assertNotEquals(first, second);
    }

    @Test
    void rejectsAStoredValueInAnUnknownFormat() {
        assertFalse(PasswordHasher.verify("plain", "plain"));
    }
}
