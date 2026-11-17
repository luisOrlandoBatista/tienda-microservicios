package com.tienda.catalogo.application.port.in;

import java.math.BigDecimal;

public record ActualizarProductoCommand(String nombre, String descripcion, BigDecimal precio) {
}