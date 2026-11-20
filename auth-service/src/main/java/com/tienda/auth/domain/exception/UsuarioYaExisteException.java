package com.tienda.auth.domain.exception;

public class UsuarioYaExisteException extends DominioException {

    public UsuarioYaExisteException(String username) {
        super("El usuario '%s' ya existe".formatted(username));
    }
}