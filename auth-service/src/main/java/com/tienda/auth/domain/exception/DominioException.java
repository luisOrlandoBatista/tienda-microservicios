package com.tienda.auth.domain.exception;

public abstract class DominioException extends RuntimeException {

    protected DominioException(String mensaje) {
        super(mensaje);
    }
}