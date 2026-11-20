package com.tienda.pedidos.domain.exception;

public class ProductoNoDisponibleException extends DominioException {

    public ProductoNoDisponibleException(Long productoId, String motivo) {
        super("El producto %d no está disponible: %s".formatted(productoId, motivo));
    }
}