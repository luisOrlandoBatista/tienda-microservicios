package com.tienda.catalogo.infrastructure.adapter.out.persistence;

import com.tienda.catalogo.application.port.out.ProductoRepositoryPort;
import com.tienda.catalogo.domain.exception.ProductoNoEncontradoException;
import com.tienda.catalogo.domain.model.Producto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductoPersistenceAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository jpaRepository;

    public ProductoPersistenceAdapter(ProductoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Producto guardar(Producto producto) {
        ProductoEntity entity = (producto.getId() == null)
                ? ProductoPersistenceMapper.toNuevaEntity(producto)
                : cargarYActualizar(producto);
        return ProductoPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ProductoPersistenceMapper::toDomain);
    }

    @Override
    public List<Producto> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(ProductoPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    private ProductoEntity cargarYActualizar(Producto producto) {
        ProductoEntity entity = jpaRepository.findById(producto.getId())
                .orElseThrow(() -> new ProductoNoEncontradoException(producto.getId()));
        ProductoPersistenceMapper.actualizarEntity(entity, producto);
        return entity;
    }
}