package com.example.basicwindowsapp.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageValidatorTest {

    @Test
    void normalizeTrimsMessage() {
        assertEquals("Hello", MessageValidator.normalize("  Hello  "));
    }

    @Test
    void normalizeRejectsBlankMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> MessageValidator.normalize(" \t "));
    }

    @Test
    void normalizeRejectsNullMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> MessageValidator.normalize(null));
    }
}
