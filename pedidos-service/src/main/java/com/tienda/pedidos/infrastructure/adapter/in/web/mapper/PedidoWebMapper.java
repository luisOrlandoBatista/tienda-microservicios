package com.tienda.pedidos.infrastructure.adapter.in.web.mapper;

import com.tienda.pedidos.application.port.in.CrearPedidoCommand;
import com.tienda.pedidos.domain.model.Pedido;
import com.tienda.pedidos.infrastructure.adapter.in.web.dto.CrearPedidoRequest;
import com.tienda.pedidos.infrastructure.adapter.in.web.dto.PedidoResponse;

public final class PedidoWebMapper {

    private PedidoWebMapper() {
    }

    public static CrearPedidoCommand toCommand(CrearPedidoRequest request, String cliente) {
        return new CrearPedidoCommand(
                cliente,
                request.lineas().stream()
                        .map(l -> new CrearPedidoCommand.LineaCommand(l.productoId(), l.cantidad()))
                        .toList());
    }

    public static PedidoResponse toResponse(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente(),
                pedido.getEstado().name(),
                pedido.total(),
                pedido.getFechaCreacion(),
                pedido.getLineas().stream()
                        .map(l -> new PedidoResponse.LineaResponse(
                                l.productoId(), l.nombreProducto(), l.precioUnitario(), l.cantidad(), l.subtotal()))
                        .toList());
    }
}