package com.tienda.pedidos.application.port.in;

import com.tienda.pedidos.domain.model.Pedido;
import reactor.core.publisher.Mono;

public interface CrearPedidoUseCase {

    Mono<Pedido> crear(CrearPedidoCommand command);
}