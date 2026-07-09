package de.hskl.rateme;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class RateMeApplicationTests {

    @Test
    void mainMethodCanBeReferenced() {
        assertDoesNotThrow(() -> RateMeApplication.class.getDeclaredMethod("main", String[].class));
    }
}
