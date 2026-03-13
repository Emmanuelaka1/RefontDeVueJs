package com.arkea.sgesapi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — DAOException.
 */
class DAOExceptionTest {

    @Test
    void constructeur_avecMessage() {
        DAOException ex = new DAOException("Erreur test");

        assertEquals("Erreur test", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void constructeur_avecMessageEtCause() {
        RuntimeException cause = new RuntimeException("Cause originale");
        DAOException ex = new DAOException("Erreur wrappée", cause);

        assertEquals("Erreur wrappée", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void constructeur_avecCauseSeule() {
        RuntimeException cause = new RuntimeException("Cause");
        DAOException ex = new DAOException(cause);

        assertEquals(cause, ex.getCause());
        assertTrue(ex.getMessage().contains("Cause"));
    }

    @Test
    void estChecked() {
        // DAOException extends Exception (checked), pas RuntimeException
        assertInstanceOf(Exception.class, new DAOException("test"));
        assertEquals(Exception.class, DAOException.class.getSuperclass());
    }
}
