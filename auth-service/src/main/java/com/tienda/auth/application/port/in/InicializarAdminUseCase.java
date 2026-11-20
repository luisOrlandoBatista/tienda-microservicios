package com.tienda.auth.application.port.in;

public interface InicializarAdminUseCase {

    void crearAdminSiNoExiste(CredencialesCommand command);
}