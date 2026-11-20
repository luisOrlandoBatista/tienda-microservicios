package com.tienda.auth.infrastructure.adapter.out.persistence;

import com.tienda.auth.domain.model.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)          // guarda "USER"/"ADMIN" como texto, no como número
    @Column(nullable = false, length = 20)
    private Rol rol;

    protected UsuarioEntity() {
    }

    UsuarioEntity(String username, String passwordHash, Rol rol) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Rol getRol() { return rol; }
}