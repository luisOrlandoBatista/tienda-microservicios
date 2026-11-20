package com.tienda.pedidos.application.service;

import com.tienda.pedidos.application.port.in.CrearPedidoCommand;
import com.tienda.pedidos.application.port.out.CatalogoPort;
import com.tienda.pedidos.application.port.out.PedidoRepositoryPort;
import com.tienda.pedidos.application.port.out.ProductoCatalogo;
import com.tienda.pedidos.domain.exception.DatoInvalidoException;
import com.tienda.pedidos.domain.exception.PedidoNoEncontradoException;
import com.tienda.pedidos.domain.exception.ProductoNoDisponibleException;
import com.tienda.pedidos.domain.model.EstadoPedido;
import com.tienda.pedidos.domain.model.LineaPedido;
import com.tienda.pedidos.domain.model.Pedido;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepositoryPort pedidoRepository;
    @Mock private CatalogoPort catalogo;
    @InjectMocks private PedidoService pedidoService;

    private final ProductoCatalogo teclado = new ProductoCatalogo(1L, "Teclado", new BigDecimal("49.99"), 10);

    private Pedido pedidoGuardado(EstadoPedido estado) {
        return Pedido.reconstruir(7L, "luis", List.of(
                        new LineaPedido(1L, "Teclado", new BigDecimal("49.99"), 2),
                        new LineaPedido(2L, "Ratón", new BigDecimal("19.90"), 1)),
                estado, Instant.now());
    }

    @Test
    void crear_usa_el_precio_de_catalogo_reduce_stock_y_guarda() {
        when(catalogo.obtenerProducto(1L)).thenReturn(Mono.just(teclado));
        when(catalogo.reducirStock(1L, 2)).thenReturn(Mono.empty());
        when(pedidoRepository.guardar(any(Pedido.class)))
                .thenAnswer(invocacion -> Mono.just(invocacion.getArgument(0)));
        var command = new CrearPedidoCommand("luis", List.of(new CrearPedidoCommand.LineaCommand(1L, 2)));

        StepVerifier.create(pedidoService.crear(command))
                .assertNext(pedido -> {
                    assertThat(pedido.getLineas().getFirst().precioUnitario()).isEqualByComparingTo("49.99");
                    assertThat(pedido.total()).isEqualByComparingTo("99.98");
                    assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CONFIRMADO);
                })
                .verifyComplete();

        verify(catalogo).reducirStock(1L, 2);
    }

    @Test
    void crear_con_un_producto_no_disponible_falla_y_no_guarda_nada() {
        when(catalogo.obtenerProducto(99L))
                .thenReturn(Mono.error(new ProductoNoDisponibleException(99L, "no existe")));
        var command = new CrearPedidoCommand("luis", List.of(new CrearPedidoCommand.LineaCommand(99L, 1)));

        StepVerifier.create(pedidoService.crear(command))
                .expectError(ProductoNoDisponibleException.class)
                .verify();

        verify(catalogo, never()).reducirStock(anyLong(), anyInt());
        verify(pedidoRepository, never()).guardar(any());
    }

    @Test
    void cancelar_devuelve_el_stock_de_cada_linea_y_guarda_el_pedido_cancelado() {
        Pedido pedido = pedidoGuardado(EstadoPedido.CONFIRMADO);
        when(pedidoRepository.buscarPorId(7L)).thenReturn(Mono.just(pedido));
        when(catalogo.aumentarStock(anyLong(), anyInt())).thenReturn(Mono.empty());
        when(pedidoRepository.guardar(pedido)).thenReturn(Mono.just(pedido));

        StepVerifier.create(pedidoService.cancelar(7L))
                .assertNext(p -> assertThat(p.getEstado()).isEqualTo(EstadoPedido.CANCELADO))
                .verifyComplete();

        verify(catalogo).aumentarStock(1L, 2);
        verify(catalogo).aumentarStock(2L, 1);
    }

    @Test
    void cancelar_un_pedido_ya_cancelado_falla_y_no_devuelve_stock() {
        when(pedidoRepository.buscarPorId(7L)).thenReturn(Mono.just(pedidoGuardado(EstadoPedido.CANCELADO)));

        StepVerifier.create(pedidoService.cancelar(7L))
                .expectError(DatoInvalidoException.class)
                .verify();

        verify(catalogo, never()).aumentarStock(anyLong(), anyInt());
    }

    @Test
    void obtener_un_pedido_inexistente_lanza_error() {
        when(pedidoRepository.buscarPorId(99L)).thenReturn(Mono.empty());

        StepVerifier.create(pedidoService.obtenerPorId(99L))
                .expectError(PedidoNoEncontradoException.class)
                .verify();
    }
}