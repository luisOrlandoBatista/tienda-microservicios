package com.tienda.auth.infrastructure.adapter.out.persistence;

import com.tienda.auth.application.port.out.UsuarioRepositoryPort;
import com.tienda.auth.domain.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioPersistenceAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity guardada = jpaRepository.save(
                new UsuarioEntity(usuario.getUsername(), usuario.getPasswordHash(), usuario.getRol()));
        return toDomain(guardada);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public boolean existePorUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    private Usuario toDomain(UsuarioEntity entity) {
        return Usuario.reconstruir(entity.getId(), entity.getUsername(), entity.getPasswordHash(), entity.getRol());
    }
}