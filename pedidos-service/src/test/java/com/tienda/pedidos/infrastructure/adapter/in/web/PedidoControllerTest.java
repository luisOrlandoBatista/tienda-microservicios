package com.tienda.pedidos.infrastructure.adapter.in.web;

import com.tienda.pedidos.application.port.in.CancelarPedidoUseCase;
import com.tienda.pedidos.application.port.in.ConsultarPedidoUseCase;
import com.tienda.pedidos.application.port.in.CrearPedidoCommand;
import com.tienda.pedidos.application.port.in.CrearPedidoUseCase;
import com.tienda.pedidos.application.port.out.CatalogoNoDisponibleException;
import com.tienda.pedidos.domain.exception.PedidoNoEncontradoException;
import com.tienda.pedidos.domain.exception.ProductoNoDisponibleException;
import com.tienda.pedidos.domain.model.EstadoPedido;
import com.tienda.pedidos.domain.model.LineaPedido;
import com.tienda.pedidos.domain.model.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private WebTestClient webTestClient;   // el "MockMvc" de WebFlux

    @MockitoBean private CrearPedidoUseCase crearPedidoUseCase;
    @MockitoBean private ConsultarPedidoUseCase consultarPedidoUseCase;
    @MockitoBean private CancelarPedidoUseCase cancelarPedidoUseCase;

    private static final String PEDIDO_VALIDO = """
            {"cliente": "luis", "lineas": [{"productoId": 1, "cantidad": 2}]}
            """;

    @Test
    void crear_devuelve_201_con_location_y_el_pedido() {
        Pedido pedido = Pedido.reconstruir(7L, "luis",
                List.of(new LineaPedido(1L, "Teclado", new BigDecimal("49.99"), 2)),
                EstadoPedido.CONFIRMADO, Instant.now());
        when(crearPedidoUseCase.crear(any(CrearPedidoCommand.class))).thenReturn(Mono.just(pedido));

        webTestClient.post().uri("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(PEDIDO_VALIDO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/pedidos/7")
                .expectBody()
                .jsonPath("$.id").isEqualTo(7)
                .jsonPath("$.estado").isEqualTo("CONFIRMADO")
                .jsonPath("$.lineas[0].nombreProducto").isEqualTo("Teclado");
    }

    @Test
    void crear_con_datos_invalidos_devuelve_400_con_los_campos() {
        webTestClient.post().uri("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"cliente": "", "lineas": []}
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errores.cliente").exists()
                .jsonPath("$.errores.lineas").exists();
    }

    @Test
    void crear_con_producto_no_disponible_devuelve_409() {
        when(crearPedidoUseCase.crear(any()))
                .thenReturn(Mono.error(new ProductoNoDisponibleException(1L, "stock insuficiente")));

        webTestClient.post().uri("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(PEDIDO_VALIDO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void crear_con_catalogo_caido_devuelve_503() {
        when(crearPedidoUseCase.crear(any()))
                .thenReturn(Mono.error(new CatalogoNoDisponibleException("caído", new RuntimeException())));

        webTestClient.post().uri("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(PEDIDO_VALIDO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Servicio no disponible");
    }

    @Test
    void obtener_un_pedido_inexistente_devuelve_404() {
        when(consultarPedidoUseCase.obtenerPorId(99L))
                .thenReturn(Mono.error(new PedidoNoEncontradoException(99L)));

        webTestClient.get().uri("/api/pedidos/99")
                .exchange()
                .expectStatus().isNotFound();
    }
}