package com.tienda.auth.application.port.in;

import com.tienda.auth.domain.model.Usuario;

public interface RegistrarUsuarioUseCase {

    Usuario registrar(CredencialesCommand command);
}