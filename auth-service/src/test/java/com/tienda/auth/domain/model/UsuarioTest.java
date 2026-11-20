package com.tienda.auth.domain.model;

import com.tienda.auth.domain.exception.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

class UsuarioTest {

    @Test
    void crear_un_usuario_valido() {
        Usuario usuario = Usuario.crear("luis", "$2a$10$hashDeEjemplo", Rol.USER);

        assertThat(usuario.getId()).isNull();
        assertThat(usuario.getRol()).isEqualTo(Rol.USER);
    }

    @Test
    void no_permite_usernames_con_caracteres_raros_o_muy_cortos() {
        assertThatThrownBy(() -> Usuario.crear("lu", "hash", Rol.USER))
                .isInstanceOf(DatoInvalidoException.class);
        assertThatThrownBy(() -> Usuario.crear("luis garcía", "hash", Rol.USER))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void la_politica_de_password_exige_8_caracteres() {
        assertThatThrownBy(() -> Usuario.comprobarPoliticaPassword("1234567"))
                .isInstanceOf(DatoInvalidoException.class);
        assertThatNoException().isThrownBy(() -> Usuario.comprobarPoliticaPassword("12345678"));
    }
}