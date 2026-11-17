package com.tienda.catalogo.domain.exception;

public class StockInsuficienteException extends DominioException {

    public StockInsuficienteException(Long productoId, int disponible, int solicitado) {
        super("Stock insuficiente para el producto %d: disponible %d, solicitado %d"
                .formatted(productoId, disponible, solicitado));
    }
}