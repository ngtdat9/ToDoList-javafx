package com.example.todolist2javafx.datamodel;

import java.time.LocalDate;
import java.util.Optional;

public final class TodoValidator {
    private TodoValidator() {
    }

    /** Returns what is wrong with the input, or empty if it can be saved. */
    public static Optional<String> validate(String shortDescription, LocalDate deadline) {
        if (shortDescription == null || shortDescription.isBlank()) {
            return Optional.of("Please enter a short description.");
        }
        if (deadline == null) {
            return Optional.of("Please choose a deadline.");
        }
        return Optional.empty();
    }
}
