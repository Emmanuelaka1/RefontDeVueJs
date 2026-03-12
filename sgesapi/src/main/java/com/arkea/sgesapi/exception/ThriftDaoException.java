package com.arkea.sgesapi.exception;

/**
 * Exception levée lors d'un problème de communication avec le DAO Thrift Topaze.
 */
public class ThriftDaoException extends RuntimeException {

    public ThriftDaoException(String message) {
        super(message);
    }

    public ThriftDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
