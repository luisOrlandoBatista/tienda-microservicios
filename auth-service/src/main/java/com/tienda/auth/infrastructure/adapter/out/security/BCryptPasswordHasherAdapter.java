package com.tienda.auth.infrastructure.adapter.out.security;

import com.tienda.auth.application.port.out.PasswordHasherPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasherAdapter implements PasswordHasherPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hashear(String passwordEnClaro) {
        return encoder.encode(passwordEnClaro);
    }

    @Override
    public boolean coincide(String passwordEnClaro, String hash) {
        return encoder.matches(passwordEnClaro, hash);
    }
}