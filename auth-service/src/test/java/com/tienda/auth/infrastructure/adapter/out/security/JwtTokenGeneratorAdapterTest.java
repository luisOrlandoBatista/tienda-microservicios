package com.tienda.auth.infrastructure.adapter.out.security;

import com.nimbusds.jose.jwk.RSAKey;
import com.tienda.auth.application.port.out.TokenGenerado;
import com.tienda.auth.domain.model.Rol;
import com.tienda.auth.domain.model.Usuario;
import com.tienda.auth.infrastructure.config.JwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenGeneratorAdapterTest {

    @Test
    void el_token_generado_se_verifica_con_la_clave_publica_y_contiene_los_datos() throws Exception {
        JwtConfig config = new JwtConfig();
        RSAKey claves = config.rsaKey();
        var adapter = new JwtTokenGeneratorAdapter(
                config.jwtEncoder(claves), "http://localhost:8083", Duration.ofHours(1));

        TokenGenerado generado = adapter.generar(Usuario.reconstruir(1L, "luis", "HASH", Rol.ADMIN));

        // Verificamos SOLO con la clave PÚBLICA, igual que harán catálogo y pedidos
        var verificador = NimbusJwtDecoder.withPublicKey(claves.toRSAPublicKey()).build();
        Jwt jwt = verificador.decode(generado.token());

        assertThat(jwt.getSubject()).isEqualTo("luis");
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("ADMIN");
        assertThat(jwt.getIssuer().toString()).isEqualTo("http://localhost:8083");
    }
}