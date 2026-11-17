package com.tienda.catalogo.application.port.in;

import com.tienda.catalogo.domain.model.Producto;

public interface ActualizarProductoUseCase {

    Producto actualizar(Long id, ActualizarProductoCommand command);
}