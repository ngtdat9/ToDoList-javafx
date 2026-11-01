package com.example.todolist2javafx.datamodel;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TodoValidatorTest {
    private static final LocalDate DEADLINE = LocalDate.of(2027, 1, 15);

    @Test
    void acceptsATitleWithADeadline() {
        assertTrue(TodoValidator.validate("Title", DEADLINE).isEmpty());
    }

    @Test
    void rejectsAMissingOrBlankTitle() {
        assertTrue(TodoValidator.validate(null, DEADLINE).isPresent());
        assertTrue(TodoValidator.validate("", DEADLINE).isPresent());
        assertTrue(TodoValidator.validate("   ", DEADLINE).isPresent());
    }

    @Test
    void rejectsAMissingDeadline() {
        assertTrue(TodoValidator.validate("Title", null).isPresent());
    }
}
