package com.tienda.auth.application.port.in;

public record CredencialesCommand(String username, String password) {
}