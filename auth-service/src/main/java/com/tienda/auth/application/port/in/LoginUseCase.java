package com.tienda.auth.application.port.in;

import com.tienda.auth.application.port.out.TokenGenerado;

public interface LoginUseCase {

    TokenGenerado login(CredencialesCommand command);
}