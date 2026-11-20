package com.tienda.catalogo.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AumentarStockRequest(@NotNull @Positive Integer cantidad) {
}