package com.tienda.pedidos.domain.model;

import com.tienda.pedidos.domain.exception.DatoInvalidoException;

import java.math.BigDecimal;

public record LineaPedido(Long productoId, String nombreProducto, BigDecimal precioUnitario, int cantidad) {

    public LineaPedido {
        if (productoId == null) {
            throw new DatoInvalidoException("El producto es obligatorio");
        }
        if (nombreProducto == null || nombreProducto.isBlank()) {
            throw new DatoInvalidoException("El nombre del producto es obligatorio");
        }
        if (precioUnitario == null || precioUnitario.signum() <= 0) {
            throw new DatoInvalidoException("El precio unitario debe ser mayor que cero");
        }
        if (cantidad <= 0) {
            throw new DatoInvalidoException("La cantidad debe ser mayor que cero");
        }
    }

    public BigDecimal subtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}