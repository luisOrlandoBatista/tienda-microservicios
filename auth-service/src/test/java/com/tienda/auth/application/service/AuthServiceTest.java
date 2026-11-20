package com.tienda.auth.application.service;

import com.tienda.auth.application.port.in.CredencialesCommand;
import com.tienda.auth.application.port.out.PasswordHasherPort;
import com.tienda.auth.application.port.out.TokenGenerado;
import com.tienda.auth.application.port.out.TokenGeneratorPort;
import com.tienda.auth.application.port.out.UsuarioRepositoryPort;
import com.tienda.auth.domain.exception.CredencialesInvalidasException;
import com.tienda.auth.domain.exception.DatoInvalidoException;
import com.tienda.auth.domain.exception.UsuarioYaExisteException;
import com.tienda.auth.domain.model.Rol;
import com.tienda.auth.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepositoryPort usuarioRepository;
    @Mock private PasswordHasherPort passwordHasher;
    @Mock private TokenGeneratorPort tokenGenerator;
    @InjectMocks private AuthService authService;

    private final CredencialesCommand luis = new CredencialesCommand("luis", "secreto123");

    @Test
    void registrar_guarda_el_hash_nunca_la_password_y_siempre_con_rol_USER() {
        when(usuarioRepository.existePorUsername("luis")).thenReturn(false);
        when(passwordHasher.hashear("secreto123")).thenReturn("HASH");
        when(usuarioRepository.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.registrar(luis);

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).guardar(guardado.capture());
        assertThat(guardado.getValue().getPasswordHash()).isEqualTo("HASH");
        assertThat(guardado.getValue().getRol()).isEqualTo(Rol.USER);
    }

    @Test
    void registrar_un_usuario_existente_falla_y_no_guarda() {
        when(usuarioRepository.existePorUsername("luis")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(luis)).isInstanceOf(UsuarioYaExisteException.class);
        verify(usuarioRepository, never()).guardar(any());
    }

    @Test
    void registrar_con_password_corta_falla_sin_llegar_a_hashear() {
        assertThatThrownBy(() -> authService.registrar(new CredencialesCommand("luis", "corta")))
                .isInstanceOf(DatoInvalidoException.class);
        verify(passwordHasher, never()).hashear(anyString());
    }

    @Test
    void login_correcto_devuelve_un_token() {
        Usuario usuario = Usuario.reconstruir(1L, "luis", "HASH", Rol.USER);
        TokenGenerado token = new TokenGenerado("eyJ...", Instant.now().plusSeconds(3600));
        when(usuarioRepository.buscarPorUsername("luis")).thenReturn(Optional.of(usuario));
        when(passwordHasher.coincide("secreto123", "HASH")).thenReturn(true);
        when(tokenGenerator.generar(usuario)).thenReturn(token);

        assertThat(authService.login(luis)).isEqualTo(token);
    }

    @Test
    void login_con_password_incorrecta_y_usuario_inexistente_dan_el_MISMO_error() {
        Usuario usuario = Usuario.reconstruir(1L, "luis", "HASH", Rol.USER);
        when(usuarioRepository.buscarPorUsername("luis")).thenReturn(Optional.of(usuario));
        when(passwordHasher.coincide("mala-password", "HASH")).thenReturn(false);
        when(usuarioRepository.buscarPorUsername("nadie")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new CredencialesCommand("luis", "mala-password")))
                .isInstanceOf(CredencialesInvalidasException.class);
        assertThatThrownBy(() -> authService.login(new CredencialesCommand("nadie", "secreto123")))
                .isInstanceOf(CredencialesInvalidasException.class);
        verify(tokenGenerator, never()).generar(any());
    }

    @Test
    void inicializar_admin_no_hace_nada_si_ya_existe() {
        when(usuarioRepository.existePorUsername("admin")).thenReturn(true);

        authService.crearAdminSiNoExiste(new CredencialesCommand("admin", "admin12345"));

        verify(usuarioRepository, never()).guardar(any());
    }
}