package com.arkea.sgesapi.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleDossierNotFound_retourne404() {
        DossierNotFoundException ex = new DossierNotFoundException("2024-PAP-001");

        ResponseEntity<Map<String, Object>> response = handler.handleDossierNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Not Found", response.getBody().get("error"));
        assertTrue(response.getBody().get("message").toString().contains("2024-PAP-001"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleDAOException_retourne500() {
        DAOException ex = new DAOException("Erreur Topaze");

        ResponseEntity<Map<String, Object>> response = handler.handleDAOException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("Erreur d'accès au système Topaze", response.getBody().get("message"));
    }

    @Test
    void handleBadRequest_retourne400() {
        IllegalArgumentException ex = new IllegalArgumentException("Paramètre invalide");

        ResponseEntity<Map<String, Object>> response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Paramètre invalide", response.getBody().get("message"));
    }

    @Test
    void handleGeneric_retourne500() {
        Exception ex = new RuntimeException("Erreur inattendue");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("Erreur technique inattendue", response.getBody().get("message"));
    }

    @Test
    void handleDAOException_avecCause_retourne500() {
        DAOException ex = new DAOException("Erreur transport", new RuntimeException("Connection refused"));

        ResponseEntity<Map<String, Object>> response = handler.handleDAOException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
