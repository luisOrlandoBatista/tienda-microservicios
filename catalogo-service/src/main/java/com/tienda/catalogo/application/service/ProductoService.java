package com.tienda.catalogo.application.service;

import com.tienda.catalogo.application.port.in.ActualizarProductoCommand;
import com.tienda.catalogo.application.port.in.ActualizarProductoUseCase;
import com.tienda.catalogo.application.port.in.ConsultarProductoUseCase;
import com.tienda.catalogo.application.port.in.CrearProductoCommand;
import com.tienda.catalogo.application.port.in.CrearProductoUseCase;
import com.tienda.catalogo.application.port.in.EliminarProductoUseCase;
import com.tienda.catalogo.application.port.in.ReducirStockUseCase;
import com.tienda.catalogo.application.port.out.ProductoRepositoryPort;
import com.tienda.catalogo.domain.exception.ProductoNoEncontradoException;
import com.tienda.catalogo.domain.model.Precio;
import com.tienda.catalogo.domain.model.Producto;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Transactional 
public class ProductoService implements CrearProductoUseCase, ConsultarProductoUseCase,
        ActualizarProductoUseCase, EliminarProductoUseCase, ReducirStockUseCase {

    private final ProductoRepositoryPort productoRepository;

    public ProductoService(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto crear(CrearProductoCommand command) {
        Producto producto = Producto.crear(
                command.nombre(), command.descripcion(), new Precio(command.precio()), command.stock());
        return productoRepository.guardar(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.buscarTodos();
    }

    @Override
    public Producto actualizar(Long id, ActualizarProductoCommand command) {
        Producto producto = obtenerPorId(id);
        producto.actualizarDatos(command.nombre(), command.descripcion(), new Precio(command.precio()));
        return productoRepository.guardar(producto);
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = obtenerPorId(id);
        productoRepository.eliminar(producto.getId());
    }

    @Override
    public Producto reducirStock(Long id, int cantidad) {
        Producto producto = obtenerPorId(id);
        producto.reducirStock(cantidad);
        return productoRepository.guardar(producto);
    }
}