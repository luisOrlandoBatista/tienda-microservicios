package com.tienda.pedidos.infrastructure.adapter.out.persistence;

import com.tienda.pedidos.application.port.out.PedidoRepositoryPort;
import com.tienda.pedidos.domain.model.Pedido;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PedidoPersistenceAdapter implements PedidoRepositoryPort {

    private final PedidoR2dbcRepository pedidoRepository;
    private final LineaPedidoR2dbcRepository lineaRepository;

    public PedidoPersistenceAdapter(PedidoR2dbcRepository pedidoRepository,
                                    LineaPedidoR2dbcRepository lineaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.lineaRepository = lineaRepository;
    }

    @Override
    public Mono<Pedido> guardar(Pedido pedido) {
        if (pedido.getId() != null) {
            // Pedido existente: solo cambia la cabecera (por ejemplo, el estado). Las líneas no cambian.
            return pedidoRepository.save(PedidoPersistenceMapper.toEntity(pedido))
                    .flatMap(this::conLineas);
        }
        // Pedido nuevo: 1) guardar la cabecera → 2) con su id, guardar las líneas → 3) devolver el dominio
        return pedidoRepository.save(PedidoPersistenceMapper.toEntity(pedido))
                .flatMap(guardado -> lineaRepository
                        .saveAll(PedidoPersistenceMapper.toLineaEntities(guardado.id(), pedido.getLineas()))
                        .collectList()
                        .map(lineas -> PedidoPersistenceMapper.toDomain(guardado, lineas)));
    }

    @Override
    public Mono<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id)      // Mono vacío si no existe
                .flatMap(this::conLineas);
    }

    @Override
    public Flux<Pedido> buscarTodos() {
        return pedidoRepository.findAll()
                .concatMap(this::conLineas);
    }

    private Mono<Pedido> conLineas(PedidoEntity entity) {
        return lineaRepository.findByPedidoId(entity.id())
                .collectList()
                .map(lineas -> PedidoPersistenceMapper.toDomain(entity, lineas));
    }
}