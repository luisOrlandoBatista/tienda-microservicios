package com.tienda.catalogo.application.port.in;

import com.tienda.catalogo.domain.model.Producto;

public interface ReducirStockUseCase {

    Producto reducirStock(Long id, int cantidad);
}