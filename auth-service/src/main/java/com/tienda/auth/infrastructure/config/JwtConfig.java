package com.tienda.auth.infrastructure.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class JwtConfig {

    /** Par de claves RSA: la PRIVADA firma, la PÚBLICA se publica para verificar. */
    @Bean
    public RSAKey rsaKey() {
        try {
            KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
            generador.initialize(2048);
            KeyPair par = generador.generateKeyPair();
            return new RSAKey.Builder((RSAPublicKey) par.getPublic())
                    .privateKey((RSAPrivateKey) par.getPrivate())
                    .keyID(UUID.randomUUID().toString())     // "kid": identificador de la clave
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo generar la clave RSA", e);
        }
    }

    /** El objeto de Spring Security que FIRMA los tokens con la clave privada. */
    @Bean
    public JwtEncoder jwtEncoder(RSAKey rsaKey) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }
}