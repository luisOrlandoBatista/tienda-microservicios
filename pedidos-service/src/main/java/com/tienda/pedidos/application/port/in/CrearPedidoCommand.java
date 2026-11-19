package com.tienda.pedidos.application.port.in;

import java.util.List;

public record CrearPedidoCommand(String cliente, List<LineaCommand> lineas) {

    public record LineaCommand(Long productoId, int cantidad) {
    }
}