package com.tienda.pedidos.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PedidoR2dbcRepository extends ReactiveCrudRepository<PedidoEntity, Long> {
}