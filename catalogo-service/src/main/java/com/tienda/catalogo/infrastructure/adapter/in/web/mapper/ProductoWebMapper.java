package com.tienda.catalogo.infrastructure.adapter.in.web.mapper;

import com.tienda.catalogo.application.port.in.ActualizarProductoCommand;
import com.tienda.catalogo.application.port.in.CrearProductoCommand;
import com.tienda.catalogo.domain.model.Producto;
import com.tienda.catalogo.infrastructure.adapter.in.web.dto.ActualizarProductoRequest;
import com.tienda.catalogo.infrastructure.adapter.in.web.dto.CrearProductoRequest;
import com.tienda.catalogo.infrastructure.adapter.in.web.dto.ProductoResponse;

public final class ProductoWebMapper {

    private ProductoWebMapper() {
    }

    public static CrearProductoCommand toCommand(CrearProductoRequest request) {
        return new CrearProductoCommand(request.nombre(), request.descripcion(), request.precio(), request.stock());
    }

    public static ActualizarProductoCommand toCommand(ActualizarProductoRequest request) {
        return new ActualizarProductoCommand(request.nombre(), request.descripcion(), request.precio());
    }

    public static ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio().valor(),
                producto.getStock());
    }
}