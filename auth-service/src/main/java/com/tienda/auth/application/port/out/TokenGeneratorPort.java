package com.tienda.auth.application.port.out;

import com.tienda.auth.domain.model.Usuario;

public interface TokenGeneratorPort {

    TokenGenerado generar(Usuario usuario);
}