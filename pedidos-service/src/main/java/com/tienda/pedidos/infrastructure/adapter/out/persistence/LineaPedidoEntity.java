package com.tienda.pedidos.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("lineas_pedido")
public record LineaPedidoEntity(
        @Id Long id,
        @Column("pedido_id") Long pedidoId,
        @Column("producto_id") Long productoId,
        @Column("nombre_producto") String nombreProducto,
        @Column("precio_unitario") BigDecimal precioUnitario,
        int cantidad) {
}