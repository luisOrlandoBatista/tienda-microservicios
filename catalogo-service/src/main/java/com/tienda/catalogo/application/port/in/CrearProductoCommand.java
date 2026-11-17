package com.tienda.catalogo.application.port.in;

import java.math.BigDecimal;

public record CrearProductoCommand(String nombre, String descripcion, BigDecimal precio, int stock) {
}