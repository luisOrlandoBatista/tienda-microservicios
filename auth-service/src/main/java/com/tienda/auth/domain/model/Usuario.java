package com.tienda.auth.domain.model;

import com.tienda.auth.domain.exception.DatoInvalidoException;

public class Usuario {

    private static final String PATRON_USERNAME = "^[a-zA-Z0-9._-]{3,50}$";
    private static final int LONGITUD_MINIMA_PASSWORD = 8;

    private final Long id;
    private final String username;
    private final String passwordHash;
    private final Rol rol;

    private Usuario(Long id, String username, String passwordHash, Rol rol) {
        if (username == null || !username.matches(PATRON_USERNAME)) {
            throw new DatoInvalidoException(
                    "El usuario debe tener entre 3 y 50 caracteres (letras, números, punto, guion o guion bajo)");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DatoInvalidoException("La contraseña es obligatoria");
        }
        if (rol == null) {
            throw new DatoInvalidoException("El rol es obligatorio");
        }
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    /** Usuario NUEVO. Recibe la contraseña YA convertida en hash: el dominio nunca ve la contraseña guardada. */
    public static Usuario crear(String username, String passwordHash, Rol rol) {
        return new Usuario(null, username, passwordHash, rol);
    }

    public static Usuario reconstruir(Long id, String username, String passwordHash, Rol rol) {
        return new Usuario(id, username, passwordHash, rol);
    }

    /** Regla de negocio sobre la contraseña EN CLARO, que se comprueba ANTES de calcular el hash. */
    public static void comprobarPoliticaPassword(String passwordEnClaro) {
        if (passwordEnClaro == null || passwordEnClaro.length() < LONGITUD_MINIMA_PASSWORD) {
            throw new DatoInvalidoException(
                    "La contraseña debe tener al menos %d caracteres".formatted(LONGITUD_MINIMA_PASSWORD));
        }
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Rol getRol() { return rol; }
}