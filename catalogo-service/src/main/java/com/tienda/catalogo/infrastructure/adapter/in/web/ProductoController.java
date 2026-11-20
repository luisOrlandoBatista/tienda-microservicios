package com.tienda.catalogo.infrastructure.adapter.in.web;

import com.tienda.catalogo.application.port.in.ActualizarProductoUseCase;
import com.tienda.catalogo.application.port.in.AumentarStockUseCase;
import com.tienda.catalogo.application.port.in.ConsultarProductoUseCase;
import com.tienda.catalogo.application.port.in.CrearProductoUseCase;
import com.tienda.catalogo.application.port.in.EliminarProductoUseCase;
import com.tienda.catalogo.application.port.in.ReducirStockUseCase;
import com.tienda.catalogo.domain.model.Producto;
import com.tienda.catalogo.infrastructure.adapter.in.web.dto.ActualizarProductoRequest;
import com.tienda.catalogo.infrastructure.adapter.in.web.dto.AumentarStockRequest;
import com.tienda.catalogo.infrastructure.adapter.in.web.dto.CrearProductoRequest;
import com.tienda.catalogo.infrastructure.adapter.in.web.dto.ProductoResponse;
import com.tienda.catalogo.infrastructure.adapter.in.web.dto.ReducirStockRequest;
import com.tienda.catalogo.infrastructure.adapter.in.web.mapper.ProductoWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final CrearProductoUseCase crearProductoUseCase;
    private final ConsultarProductoUseCase consultarProductoUseCase;
    private final ActualizarProductoUseCase actualizarProductoUseCase;
    private final EliminarProductoUseCase eliminarProductoUseCase;
    private final ReducirStockUseCase reducirStockUseCase;
    private final AumentarStockUseCase aumentarStockUseCase;

    public ProductoController(CrearProductoUseCase crearProductoUseCase,
                              ConsultarProductoUseCase consultarProductoUseCase,
                              ActualizarProductoUseCase actualizarProductoUseCase,
                              EliminarProductoUseCase eliminarProductoUseCase,
                              ReducirStockUseCase reducirStockUseCase,
                              AumentarStockUseCase aumentarStockUseCase) {
        this.crearProductoUseCase = crearProductoUseCase;
        this.consultarProductoUseCase = consultarProductoUseCase;
        this.actualizarProductoUseCase = actualizarProductoUseCase;
        this.eliminarProductoUseCase = eliminarProductoUseCase;
        this.reducirStockUseCase = reducirStockUseCase;
        this.aumentarStockUseCase = aumentarStockUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody CrearProductoRequest request) {
        Producto creado = crearProductoUseCase.crear(ProductoWebMapper.toCommand(request));
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(ubicacion).body(ProductoWebMapper.toResponse(creado));
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return consultarProductoUseCase.listar().stream()
                .map(ProductoWebMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return ProductoWebMapper.toResponse(consultarProductoUseCase.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id,
                                       @Valid @RequestBody ActualizarProductoRequest request) {
        return ProductoWebMapper.toResponse(
                actualizarProductoUseCase.actualizar(id, ProductoWebMapper.toCommand(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        eliminarProductoUseCase.eliminar(id);
    }

    @PostMapping("/{id}/reducir-stock")
    public ProductoResponse reducirStock(@PathVariable Long id,
                                         @Valid @RequestBody ReducirStockRequest request) {
        return ProductoWebMapper.toResponse(reducirStockUseCase.reducirStock(id, request.cantidad()));
    }

    @PostMapping("/{id}/aumentar-stock")
    public ProductoResponse aumentarStock(@PathVariable Long id,
                                          @Valid @RequestBody AumentarStockRequest request) {
        return ProductoWebMapper.toResponse(aumentarStockUseCase.aumentarStock(id, request.cantidad()));
    }
}