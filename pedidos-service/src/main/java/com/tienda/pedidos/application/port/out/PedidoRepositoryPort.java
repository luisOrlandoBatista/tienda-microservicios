package com.tienda.pedidos.application.port.out;

import com.tienda.pedidos.domain.model.Pedido;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PedidoRepositoryPort {

    Mono<Pedido> guardar(Pedido pedido);

    Mono<Pedido> buscarPorId(Long id);

    Flux<Pedido> buscarTodos();
} 