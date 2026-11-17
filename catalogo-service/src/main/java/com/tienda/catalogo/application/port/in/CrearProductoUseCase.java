package com.tienda.catalogo.application.port.in;

import com.tienda.catalogo.domain.model.Producto;

public interface CrearProductoUseCase {

    Producto crear(CrearProductoCommand command);
}