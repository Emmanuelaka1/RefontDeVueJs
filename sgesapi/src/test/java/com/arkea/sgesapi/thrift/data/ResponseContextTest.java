package com.arkea.sgesapi.thrift.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — ResponseContext.
 */
class ResponseContextTest {

    @Test
    void nouveauContext_vide() {
        ResponseContext ctx = new ResponseContext();

        assertTrue(ctx.getMessages().isEmpty());
        assertFalse(ctx.hasErrors());
        assertNull(ctx.getFirstError());
    }

    @Test
    void addMessage_error_detecteErreur() {
        ResponseContext ctx = new ResponseContext();
        ctx.addMessage(ResponseType.ERROR, "Personne introuvable");

        assertTrue(ctx.hasErrors());
        assertTrue(ctx.containsKey(ResponseType.ERROR));
        assertEquals("Personne introuvable", ctx.getFirstError());
    }

    @Test
    void addMessage_plusieursErreurs_getFirstRetournePremiere() {
        ResponseContext ctx = new ResponseContext();
        ctx.addMessage(ResponseType.ERROR, "Erreur 1");
        ctx.addMessage(ResponseType.ERROR, "Erreur 2");

        assertEquals("Erreur 1", ctx.getFirstError());
        assertEquals(2, ctx.getMessagesByType(ResponseType.ERROR).size());
    }

    @Test
    void addMessage_warning_pasErreur() {
        ResponseContext ctx = new ResponseContext();
        ctx.addMessage(ResponseType.WARNING, "Attention données anciennes");

        assertFalse(ctx.hasErrors());
        assertTrue(ctx.containsKey(ResponseType.WARNING));
        assertEquals(1, ctx.getMessagesByType(ResponseType.WARNING).size());
    }

    @Test
    void addMessage_success_pasErreur() {
        ResponseContext ctx = new ResponseContext();
        ctx.addMessage(ResponseType.SUCCESS, "Opération réussie");

        assertFalse(ctx.hasErrors());
        assertTrue(ctx.containsKey(ResponseType.SUCCESS));
    }

    @Test
    void addMessage_info() {
        ResponseContext ctx = new ResponseContext();
        ctx.addMessage(ResponseType.INFO, "Traitement en cours");

        assertTrue(ctx.containsKey(ResponseType.INFO));
        assertEquals("Traitement en cours", ctx.getMessagesByType(ResponseType.INFO).get(0));
    }

    @Test
    void containsKey_typAbsent_retourneFalse() {
        ResponseContext ctx = new ResponseContext();

        assertFalse(ctx.containsKey(ResponseType.ERROR));
        assertFalse(ctx.containsKey(ResponseType.WARNING));
    }

    @Test
    void getMessagesByType_typAbsent_retourneListeVide() {
        ResponseContext ctx = new ResponseContext();

        List<String> messages = ctx.getMessagesByType(ResponseType.ERROR);

        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    void getFirstError_sansErreur_retourneNull() {
        ResponseContext ctx = new ResponseContext();
        ctx.addMessage(ResponseType.SUCCESS, "OK");

        assertNull(ctx.getFirstError());
    }

    @Test
    void multipleTypes_independants() {
        ResponseContext ctx = new ResponseContext();
        ctx.addMessage(ResponseType.ERROR, "Erreur");
        ctx.addMessage(ResponseType.WARNING, "Attention");
        ctx.addMessage(ResponseType.INFO, "Info");
        ctx.addMessage(ResponseType.SUCCESS, "OK");

        assertEquals(4, ctx.getMessages().size());
        assertTrue(ctx.hasErrors());
        assertEquals(1, ctx.getMessagesByType(ResponseType.ERROR).size());
        assertEquals(1, ctx.getMessagesByType(ResponseType.WARNING).size());
    }

    @Test
    void toString_contientMessages() {
        ResponseContext ctx = new ResponseContext();
        ctx.addMessage(ResponseType.ERROR, "Test");

        String str = ctx.toString();

        assertTrue(str.contains("ResponseContext"));
        assertTrue(str.contains("ERROR"));
    }
}
