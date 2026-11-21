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

import com.tienda.pedidos.infrastructure.config.SecurityConfig;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(PedidoController.class)
@Import(SecurityConfig.class)
class PedidoControllerTest {

    @Autowired
    private WebTestClient webTestClient;   // el "MockMvc" de WebFlux

    @MockitoBean private CrearPedidoUseCase crearPedidoUseCase;
    @MockitoBean private ConsultarPedidoUseCase consultarPedidoUseCase;
    @MockitoBean private CancelarPedidoUseCase cancelarPedidoUseCase;
    @MockitoBean private ReactiveJwtDecoder jwtDecoder;   // evita descargar claves de auth-service

    /** Un WebTestClient que actúa como si "luis" hubiera hecho login. */
    private WebTestClient comoLuis() {
        return webTestClient.mutateWith(mockJwt()
                .jwt(jwt -> jwt.subject("luis"))
                .authorities(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private static final String PEDIDO_VALIDO = """
            {"lineas": [{"productoId": 1, "cantidad": 2}]}
            """;

    @Test
    void crear_devuelve_201_con_location_y_el_pedido() {
        Pedido pedido = Pedido.reconstruir(7L, "luis",
                List.of(new LineaPedido(1L, "Teclado", new BigDecimal("49.99"), 2)),
                EstadoPedido.CONFIRMADO, Instant.now());
        when(crearPedidoUseCase.crear(any(CrearPedidoCommand.class))).thenReturn(Mono.just(pedido));

        comoLuis().post().uri("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(PEDIDO_VALIDO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/pedidos/7")
                .expectBody()
                .jsonPath("$.id").isEqualTo(7)
                .jsonPath("$.estado").isEqualTo("CONFIRMADO")
                .jsonPath("$.lineas[0].nombreProducto").isEqualTo("Teclado");

        ArgumentCaptor<CrearPedidoCommand> command = ArgumentCaptor.forClass(CrearPedidoCommand.class);
        verify(crearPedidoUseCase).crear(command.capture());
        assertThat(command.getValue().cliente()).isEqualTo("luis");   // ¡el cliente viene del TOKEN!
    }

    @Test
    void crear_con_datos_invalidos_devuelve_400_con_los_campos() {
        comoLuis().post().uri("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("""
                        {"lineas": []}
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errores.lineas").exists();
    }

    @Test
    void crear_con_producto_no_disponible_devuelve_409() {
        when(crearPedidoUseCase.crear(any()))
                .thenReturn(Mono.error(new ProductoNoDisponibleException(1L, "stock insuficiente")));

        comoLuis().post().uri("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(PEDIDO_VALIDO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void crear_con_catalogo_caido_devuelve_503() {
        when(crearPedidoUseCase.crear(any()))
                .thenReturn(Mono.error(new CatalogoNoDisponibleException("caído", new RuntimeException())));

        comoLuis().post().uri("/api/pedidos")
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

        comoLuis().get().uri("/api/pedidos/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void crear_sin_token_devuelve_401() {
        webTestClient.post().uri("/api/pedidos")          // sin comoLuis(): no hay token
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(PEDIDO_VALIDO)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void listar_todos_los_pedidos_con_un_usuario_normal_devuelve_403() {
        comoLuis().get().uri("/api/pedidos")
                .exchange()
                .expectStatus().isForbidden();
    }
}