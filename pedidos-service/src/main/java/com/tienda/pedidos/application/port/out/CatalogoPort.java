package com.tienda.pedidos.application.port.out;

import reactor.core.publisher.Mono;

public interface CatalogoPort {

    Mono<ProductoCatalogo> obtenerProducto(Long productoId);

    Mono<Void> reducirStock(Long productoId, int cantidad);

    Mono<Void> aumentarStock(Long productoId, int cantidad);
}