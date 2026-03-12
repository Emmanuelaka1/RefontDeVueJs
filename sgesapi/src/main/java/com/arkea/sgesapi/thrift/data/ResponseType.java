package com.arkea.sgesapi.thrift.data;

/**
 * Types de réponse dans un ResponseContext Thrift.
 * <p>
 * Equivalent interne de {@code com.arkea.commons.thrift.thrift.data.ResponseType}
 * utilisé dans l'écosystème Catalyst Arkea.
 */
public enum ResponseType {

    /** Réponse en succès */
    SUCCESS,

    /** Réponse en erreur métier */
    ERROR,

    /** Réponse en avertissement */
    WARNING,

    /** Réponse informative */
    INFO
}
