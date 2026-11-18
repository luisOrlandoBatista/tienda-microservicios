package com.tienda.catalogo.infrastructure.config;

import com.tienda.catalogo.application.port.out.ProductoRepositoryPort;
import com.tienda.catalogo.application.service.ProductoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ProductoService productoService(ProductoRepositoryPort productoRepositoryPort) {
        return new ProductoService(productoRepositoryPort);
    }
}