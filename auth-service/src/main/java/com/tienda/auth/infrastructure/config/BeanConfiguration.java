package com.tienda.auth.infrastructure.config;

import com.tienda.auth.application.port.out.PasswordHasherPort;
import com.tienda.auth.application.port.out.TokenGeneratorPort;
import com.tienda.auth.application.port.out.UsuarioRepositoryPort;
import com.tienda.auth.application.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public AuthService authService(UsuarioRepositoryPort usuarioRepositoryPort,
                                   PasswordHasherPort passwordHasherPort,
                                   TokenGeneratorPort tokenGeneratorPort) {
        return new AuthService(usuarioRepositoryPort, passwordHasherPort, tokenGeneratorPort);
    }
}