package com.tienda.pedidos.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PedidoResponse(
        Long id,
        String cliente,
        String estado,
        BigDecimal total,
        Instant fechaCreacion,
        List<LineaResponse> lineas) {

    public record LineaResponse(
            Long productoId,
            String nombreProducto,
            BigDecimal precioUnitario,
            int cantidad,
            BigDecimal subtotal) {
    }
}