package com.arkea.sgesapi.exception;

/**
 * Exception levée par la couche DAO lors d'un problème d'accès aux données.
 * <p>
 * Equivalent interne de {@code com.arkea.catalyst.utils.exception.DAOException}
 * utilisé dans l'écosystème Catalyst Arkea.
 * <p>
 * Encapsule les erreurs Thrift (transport, protocole, métier) et les
 * traduit en exception Java standard pour la couche service.
 */
public class DAOException extends Exception {

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }
}
