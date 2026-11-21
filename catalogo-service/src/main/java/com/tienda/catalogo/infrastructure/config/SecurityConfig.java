package com.tienda.catalogo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Documentación y página de error: públicas
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                        // Consultar el catálogo: público
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        // Mover stock: cualquier usuario autenticado (lo usa pedidos-service)
                        .requestMatchers(HttpMethod.POST,
                                "/api/productos/*/reducir-stock", "/api/productos/*/aumentar-stock").authenticated()
                        // Crear, modificar y borrar productos: solo ADMIN
                        .requestMatchers("/api/productos/**").hasRole("ADMIN")
                        // Todo lo demás: prohibido
                        .anyRequest().denyAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}