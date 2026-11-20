package com.tienda.auth.domain.exception;

public class CredencialesInvalidasException extends DominioException {

    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos");
    }
}