package com.arkea.sgesapi.thrift.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Contexte de réponse retourné par les services Thrift Topaze.
 * <p>
 * Equivalent interne de {@code com.arkea.commons.thrift.thrift.data.ResponseContext}
 * utilisé dans l'écosystème Catalyst Arkea.
 * <p>
 * Contient les messages classés par type (SUCCESS, ERROR, WARNING, INFO).
 * Permet de détecter les erreurs métier retournées par Topaze même quand
 * l'appel Thrift lui-même n'a pas levé d'exception.
 */
public class ResponseContext {

    private final Map<ResponseType, List<String>> messages;

    public ResponseContext() {
        this.messages = new EnumMap<>(ResponseType.class);
    }

    /**
     * Retourne la map complète des messages par type.
     */
    public Map<ResponseType, List<String>> getMessages() {
        return messages;
    }

    /**
     * Ajoute un message pour un type donné.
     *
     * @param type    type de message (ERROR, WARNING, etc.)
     * @param message texte du message
     */
    public void addMessage(ResponseType type, String message) {
        messages.computeIfAbsent(type, k -> new ArrayList<>()).add(message);
    }

    /**
     * Vérifie si la réponse contient des messages d'un type donné.
     *
     * @param type le type à vérifier
     * @return true si au moins un message de ce type existe
     */
    public boolean containsKey(ResponseType type) {
        return messages.containsKey(type) && !messages.get(type).isEmpty();
    }

    /**
     * Retourne les messages d'un type donné.
     *
     * @param type le type de messages à récupérer
     * @return liste des messages (vide si aucun)
     */
    public List<String> getMessagesByType(ResponseType type) {
        return messages.getOrDefault(type, List.of());
    }

    /**
     * Vérifie si la réponse contient des erreurs.
     *
     * @return true si au moins une erreur est présente
     */
    public boolean hasErrors() {
        return containsKey(ResponseType.ERROR);
    }

    /**
     * Retourne le premier message d'erreur ou null.
     */
    public String getFirstError() {
        List<String> errors = getMessagesByType(ResponseType.ERROR);
        return errors.isEmpty() ? null : errors.get(0);
    }

    @Override
    public String toString() {
        return "ResponseContext{" + messages + '}';
    }
}
