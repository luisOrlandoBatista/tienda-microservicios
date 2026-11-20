package com.tienda.auth.application.service;

import com.tienda.auth.application.port.in.CredencialesCommand;
import com.tienda.auth.application.port.in.InicializarAdminUseCase;
import com.tienda.auth.application.port.in.LoginUseCase;
import com.tienda.auth.application.port.in.RegistrarUsuarioUseCase;
import com.tienda.auth.application.port.out.PasswordHasherPort;
import com.tienda.auth.application.port.out.TokenGenerado;
import com.tienda.auth.application.port.out.TokenGeneratorPort;
import com.tienda.auth.application.port.out.UsuarioRepositoryPort;
import com.tienda.auth.domain.exception.CredencialesInvalidasException;
import com.tienda.auth.domain.exception.UsuarioYaExisteException;
import com.tienda.auth.domain.model.Rol;
import com.tienda.auth.domain.model.Usuario;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AuthService implements RegistrarUsuarioUseCase, LoginUseCase, InicializarAdminUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenGeneratorPort tokenGenerator;

    public AuthService(UsuarioRepositoryPort usuarioRepository,
                       PasswordHasherPort passwordHasher,
                       TokenGeneratorPort tokenGenerator) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Usuario registrar(CredencialesCommand command) {
        return crearUsuario(command, Rol.USER);       // el registro público SIEMPRE es USER
    }

    @Override
    @Transactional(readOnly = true)
    public TokenGenerado login(CredencialesCommand command) {
        Usuario usuario = usuarioRepository.buscarPorUsername(command.username())
                .filter(u -> passwordHasher.coincide(command.password(), u.getPasswordHash()))
                .orElseThrow(CredencialesInvalidasException::new);
        return tokenGenerator.generar(usuario);
    }

    @Override
    public void crearAdminSiNoExiste(CredencialesCommand command) {
        if (!usuarioRepository.existePorUsername(command.username())) {
            crearUsuario(command, Rol.ADMIN);
        }
    }

    private Usuario crearUsuario(CredencialesCommand command, Rol rol) {
        Usuario.comprobarPoliticaPassword(command.password());          // 1. regla de negocio (dominio)
        if (usuarioRepository.existePorUsername(command.username())) {  // 2. no duplicados
            throw new UsuarioYaExisteException(command.username());
        }
        String hash = passwordHasher.hashear(command.password());       // 3. hashear (puerto)
        return usuarioRepository.guardar(Usuario.crear(command.username(), hash, rol));  // 4. guardar
    }
}