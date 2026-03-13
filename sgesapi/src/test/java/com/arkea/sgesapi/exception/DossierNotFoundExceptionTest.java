package com.arkea.sgesapi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — DossierNotFoundException.
 */
class DossierNotFoundExceptionTest {

    @Test
    void constructeur_inclutNumeroPret() {
        DossierNotFoundException ex = new DossierNotFoundException("2024-PAP-001");

        assertTrue(ex.getMessage().contains("2024-PAP-001"));
    }

    @Test
    void estRuntime() {
        DossierNotFoundException ex = new DossierNotFoundException("test");

        assertInstanceOf(RuntimeException.class, ex);
    }
}
