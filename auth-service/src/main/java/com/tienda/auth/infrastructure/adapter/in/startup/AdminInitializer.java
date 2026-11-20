package com.tienda.auth.infrastructure.adapter.in.startup;

import com.tienda.auth.application.port.in.CredencialesCommand;
import com.tienda.auth.application.port.in.InicializarAdminUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final InicializarAdminUseCase inicializarAdminUseCase;
    private final String username;
    private final String password;

    public AdminInitializer(InicializarAdminUseCase inicializarAdminUseCase,
                            @Value("${auth.admin.username}") String username,
                            @Value("${auth.admin.password}") String password) {
        this.inicializarAdminUseCase = inicializarAdminUseCase;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        inicializarAdminUseCase.crearAdminSiNoExiste(new CredencialesCommand(username, password));
        log.info("Usuario administrador '{}' disponible", username);
    }
}