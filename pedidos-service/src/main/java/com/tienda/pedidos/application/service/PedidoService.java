package com.tienda.pedidos.application.service;

import com.tienda.pedidos.application.port.in.ConsultarPedidoUseCase;
import com.tienda.pedidos.application.port.in.CrearPedidoCommand;
import com.tienda.pedidos.application.port.in.CrearPedidoUseCase;
import com.tienda.pedidos.application.port.out.CatalogoPort;
import com.tienda.pedidos.application.port.out.PedidoRepositoryPort;
import com.tienda.pedidos.domain.exception.DatoInvalidoException;
import com.tienda.pedidos.domain.exception.PedidoNoEncontradoException;
import com.tienda.pedidos.domain.model.LineaPedido;
import com.tienda.pedidos.domain.model.Pedido;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Transactional
public class PedidoService implements CrearPedidoUseCase, ConsultarPedidoUseCase {

    private final PedidoRepositoryPort pedidoRepository;
    private final CatalogoPort catalogo;

    public PedidoService(PedidoRepositoryPort pedidoRepository, CatalogoPort catalogo) {
        this.pedidoRepository = pedidoRepository;
        this.catalogo = catalogo;
    }

    @Override
    public Mono<Pedido> crear(CrearPedidoCommand command) {
        if (command.lineas() == null || command.lineas().isEmpty()) {
            return Mono.error(new DatoInvalidoException("Un pedido debe tener al menos una línea"));
        }
        return Flux.fromIterable(command.lineas())           // 1. recorre las líneas pedidas
                .concatMap(this::prepararLinea)              // 2. por cada una: consulta catálogo y reduce stock
                .collectList()                               // 3. junta todas las LineaPedido en una lista
                .map(lineas -> Pedido.crear(command.cliente(), lineas))   // 4. el DOMINIO crea el pedido
                .flatMap(pedidoRepository::guardar);         // 5. lo guarda (operación asíncrona → flatMap)
    }

    private Mono<LineaPedido> prepararLinea(CrearPedidoCommand.LineaCommand linea) {
        return catalogo.obtenerProducto(linea.productoId())
                .flatMap(producto -> catalogo.reducirStock(producto.id(), linea.cantidad())
                        .thenReturn(new LineaPedido(
                                producto.id(), producto.nombre(), producto.precio(), linea.cantidad())));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.buscarPorId(id)
                .switchIfEmpty(Mono.error(new PedidoNoEncontradoException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Pedido> listar() {
        return pedidoRepository.buscarTodos();
    }
}