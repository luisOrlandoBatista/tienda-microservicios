package com.tienda.auth.application.port.out;

public interface PasswordHasherPort {

    String hashear(String passwordEnClaro);

    boolean coincide(String passwordEnClaro, String hash);
}