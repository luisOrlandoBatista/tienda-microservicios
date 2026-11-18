package com.tienda.catalogo.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public record ProductoResponse(Long id, String nombre, String descripcion, BigDecimal precio, int stock) {
}