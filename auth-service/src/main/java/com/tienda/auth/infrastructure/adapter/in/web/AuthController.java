package com.tienda.auth.infrastructure.adapter.in.web;

import com.tienda.auth.application.port.in.CredencialesCommand;
import com.tienda.auth.application.port.in.LoginUseCase;
import com.tienda.auth.application.port.in.RegistrarUsuarioUseCase;
import com.tienda.auth.application.port.out.TokenGenerado;
import com.tienda.auth.domain.model.Usuario;
import com.tienda.auth.infrastructure.adapter.in.web.dto.CredencialesRequest;
import com.tienda.auth.infrastructure.adapter.in.web.dto.TokenResponse;
import com.tienda.auth.infrastructure.adapter.in.web.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegistrarUsuarioUseCase registrarUsuarioUseCase, LoginUseCase loginUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody CredencialesRequest request) {
        Usuario usuario = registrarUsuarioUseCase.registrar(toCommand(request));
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getRol().name());
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody CredencialesRequest request) {
        TokenGenerado token = loginUseCase.login(toCommand(request));
        return new TokenResponse(token.token(), "Bearer", token.expiraEn());
    }

    private CredencialesCommand toCommand(CredencialesRequest request) {
        return new CredencialesCommand(request.username(), request.password());
    }
}