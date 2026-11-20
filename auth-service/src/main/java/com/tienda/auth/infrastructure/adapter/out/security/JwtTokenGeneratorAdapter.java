package com.tienda.auth.infrastructure.adapter.out.security;

import com.tienda.auth.application.port.out.TokenGenerado;
import com.tienda.auth.application.port.out.TokenGeneratorPort;
import com.tienda.auth.domain.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    private final JwtEncoder jwtEncoder;
    private final String emisor;
    private final Duration duracion;

    public JwtTokenGeneratorAdapter(JwtEncoder jwtEncoder,
                                    @Value("${auth.jwt.emisor}") String emisor,
                                    @Value("${auth.jwt.duracion}") Duration duracion) {
        this.jwtEncoder = jwtEncoder;
        this.emisor = emisor;
        this.duracion = duracion;
    }

    @Override
    public TokenGenerado generar(Usuario usuario) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(duracion);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(emisor)                                      // iss: quién lo emite
                .subject(usuario.getUsername())                      // sub: de quién es
                .issuedAt(ahora)                                     // iat: cuándo se emitió
                .expiresAt(expira)                                   // exp: cuándo caduca
                .claim("roles", List.of(usuario.getRol().name()))    // dato propio: sus roles
                .build();

        JwsHeader cabecera = JwsHeader.with(SignatureAlgorithm.RS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
        return new TokenGenerado(token, expira);
    }
}