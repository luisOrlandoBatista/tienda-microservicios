package com.tienda.catalogo.domain.exception;

public class ProductoNoEncontradoException extends DominioException {

    public ProductoNoEncontradoException(Long id) {
        super("No existe el producto con id %d".formatted(id));
    }
}