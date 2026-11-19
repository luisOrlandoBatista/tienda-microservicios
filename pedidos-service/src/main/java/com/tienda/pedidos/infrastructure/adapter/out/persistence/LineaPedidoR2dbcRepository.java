package com.tienda.pedidos.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface LineaPedidoR2dbcRepository extends ReactiveCrudRepository<LineaPedidoEntity, Long> {

    Flux<LineaPedidoEntity> findByPedidoId(Long pedidoId);
}