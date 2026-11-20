package com.tienda.pedidos.domain.exception;

public class PedidoNoEncontradoException extends DominioException {

    public PedidoNoEncontradoException(Long id) {
        super("No existe el pedido con id %d".formatted(id));
    }
}