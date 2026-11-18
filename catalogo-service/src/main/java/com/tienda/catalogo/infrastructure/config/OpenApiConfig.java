package com.tienda.catalogo.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogoOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Catálogo Service API")
                .description("Gestión de productos y stock de la tienda")
                .version("v1"));
    }
}