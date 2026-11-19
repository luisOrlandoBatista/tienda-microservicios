package com.tienda.pedidos.application.port.in;

import com.tienda.pedidos.domain.model.Pedido;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConsultarPedidoUseCase {

    Mono<Pedido> obtenerPorId(Long id);

    Flux<Pedido> listar();
}