package com.tienda.pedidos.application.port.out;

import java.math.BigDecimal;

public record ProductoCatalogo(Long id, String nombre, BigDecimal precio, int stock) {
}