package com.tienda.auth.application.port.out;

import com.tienda.auth.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorUsername(String username);

    boolean existePorUsername(String username);
}