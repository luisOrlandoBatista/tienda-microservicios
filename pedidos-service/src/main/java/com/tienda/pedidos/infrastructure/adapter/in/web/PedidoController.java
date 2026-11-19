package com.tienda.pedidos.infrastructure.adapter.in.web;

import com.tienda.pedidos.application.port.in.ConsultarPedidoUseCase;
import com.tienda.pedidos.application.port.in.CrearPedidoUseCase;
import com.tienda.pedidos.infrastructure.adapter.in.web.dto.CrearPedidoRequest;
import com.tienda.pedidos.infrastructure.adapter.in.web.dto.PedidoResponse;
import com.tienda.pedidos.infrastructure.adapter.in.web.mapper.PedidoWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final CrearPedidoUseCase crearPedidoUseCase;
    private final ConsultarPedidoUseCase consultarPedidoUseCase;

    public PedidoController(CrearPedidoUseCase crearPedidoUseCase,
                            ConsultarPedidoUseCase consultarPedidoUseCase) {
        this.crearPedidoUseCase = crearPedidoUseCase;
        this.consultarPedidoUseCase = consultarPedidoUseCase;
    }

    @PostMapping
    public Mono<ResponseEntity<PedidoResponse>> crear(@Valid @RequestBody CrearPedidoRequest request) {
        return crearPedidoUseCase.crear(PedidoWebMapper.toCommand(request))
                .map(pedido -> ResponseEntity
                        .created(URI.create("/api/pedidos/" + pedido.getId()))
                        .body(PedidoWebMapper.toResponse(pedido)));
    }

    @GetMapping("/{id}")
    public Mono<PedidoResponse> obtener(@PathVariable Long id) {
        return consultarPedidoUseCase.obtenerPorId(id)
                .map(PedidoWebMapper::toResponse);
    }

    @GetMapping
    public Flux<PedidoResponse> listar() {
        return consultarPedidoUseCase.listar()
                .map(PedidoWebMapper::toResponse);
    }
}