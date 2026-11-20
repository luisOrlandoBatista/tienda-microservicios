package com.tienda.pedidos.infrastructure.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrearPedidoRequest(
        @NotBlank @Size(max = 100) String cliente,
        @NotEmpty List<@Valid LineaRequest> lineas) {

    public record LineaRequest(
            @NotNull Long productoId,
            @NotNull @Positive Integer cantidad) {
    }
}