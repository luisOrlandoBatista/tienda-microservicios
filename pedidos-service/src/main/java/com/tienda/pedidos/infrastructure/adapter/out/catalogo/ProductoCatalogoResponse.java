package com.tienda.pedidos.infrastructure.adapter.out.catalogo;

import java.math.BigDecimal;

public record ProductoCatalogoResponse(Long id, String nombre, BigDecimal precio, int stock) {
}