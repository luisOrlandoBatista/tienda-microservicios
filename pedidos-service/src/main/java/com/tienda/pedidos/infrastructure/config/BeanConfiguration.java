package com.tienda.pedidos.infrastructure.config;

import com.tienda.pedidos.application.port.out.CatalogoPort;
import com.tienda.pedidos.application.port.out.PedidoRepositoryPort;
import com.tienda.pedidos.application.service.PedidoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public PedidoService pedidoService(PedidoRepositoryPort pedidoRepositoryPort, CatalogoPort catalogoPort) {
        return new PedidoService(pedidoRepositoryPort, catalogoPort);
    }
}