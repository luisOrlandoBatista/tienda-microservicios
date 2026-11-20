package com.tienda.catalogo.application.port.in;

import com.tienda.catalogo.domain.model.Producto;

public interface AumentarStockUseCase {

    Producto aumentarStock(Long id, int cantidad);
}