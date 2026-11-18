package com.tienda.catalogo.infrastructure.adapter.out.persistence;

import com.tienda.catalogo.domain.model.Precio;
import com.tienda.catalogo.domain.model.Producto;

final class ProductoPersistenceMapper {

    private ProductoPersistenceMapper() {
        // clase de utilidad: no se instancia
    }

    static Producto toDomain(ProductoEntity entity) {
        return Producto.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                new Precio(entity.getPrecio()),
                entity.getStock());
    }

    static ProductoEntity toNuevaEntity(Producto producto) {
        ProductoEntity entity = new ProductoEntity();
        actualizarEntity(entity, producto);
        return entity;
    }

    static void actualizarEntity(ProductoEntity entity, Producto producto) {
        entity.setNombre(producto.getNombre());
        entity.setDescripcion(producto.getDescripcion());
        entity.setPrecio(producto.getPrecio().valor());
        entity.setStock(producto.getStock());
    }
}