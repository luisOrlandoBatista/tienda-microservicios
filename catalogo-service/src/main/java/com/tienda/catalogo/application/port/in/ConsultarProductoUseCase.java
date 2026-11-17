package com.tienda.catalogo.application.port.in;

import com.tienda.catalogo.domain.model.Producto;

import java.util.List;

public interface ConsultarProductoUseCase {

    Producto obtenerPorId(Long id);

    List<Producto> listar();
}