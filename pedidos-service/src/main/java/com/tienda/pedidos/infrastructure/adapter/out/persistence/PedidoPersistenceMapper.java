package com.tienda.pedidos.infrastructure.adapter.out.persistence;

import com.tienda.pedidos.domain.model.EstadoPedido;
import com.tienda.pedidos.domain.model.LineaPedido;
import com.tienda.pedidos.domain.model.Pedido;

import java.util.List;

final class PedidoPersistenceMapper {

    private PedidoPersistenceMapper() {
    }

    static PedidoEntity toEntity(Pedido pedido) {
        return new PedidoEntity(
                pedido.getId(),
                pedido.getCliente(),
                pedido.getEstado().name(),
                pedido.total(),
                pedido.getFechaCreacion());
    }

    static List<LineaPedidoEntity> toLineaEntities(Long pedidoId, List<LineaPedido> lineas) {
        return lineas.stream()
                .map(l -> new LineaPedidoEntity(
                        null, pedidoId, l.productoId(), l.nombreProducto(), l.precioUnitario(), l.cantidad()))
                .toList();
    }

    static Pedido toDomain(PedidoEntity entity, List<LineaPedidoEntity> lineas) {
        return Pedido.reconstruir(
                entity.id(),
                entity.cliente(),
                lineas.stream()
                        .map(l -> new LineaPedido(
                                l.productoId(), l.nombreProducto(), l.precioUnitario(), l.cantidad()))
                        .toList(),
                EstadoPedido.valueOf(entity.estado()),
                entity.fechaCreacion());
    }
}