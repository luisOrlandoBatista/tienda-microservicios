package com.tienda.catalogo.application.port.out;

import com.tienda.catalogo.domain.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {

    Producto guardar(Producto producto);

    Optional<Producto> buscarPorId(Long id);

    List<Producto> buscarTodos();

    void eliminar(Long id);
}