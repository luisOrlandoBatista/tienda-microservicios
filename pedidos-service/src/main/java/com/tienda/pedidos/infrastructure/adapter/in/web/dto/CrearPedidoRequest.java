package com.tienda.pedidos.infrastructure.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CrearPedidoRequest(@NotEmpty List<@Valid LineaRequest> lineas) {

    public record LineaRequest(
            @NotNull Long productoId,
            @NotNull @Positive Integer cantidad) {
    }
}