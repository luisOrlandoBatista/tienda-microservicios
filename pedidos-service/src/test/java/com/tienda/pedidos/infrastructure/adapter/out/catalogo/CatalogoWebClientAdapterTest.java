package com.tienda.pedidos.infrastructure.adapter.out.catalogo;

import com.tienda.pedidos.application.port.out.CatalogoNoDisponibleException;
import com.tienda.pedidos.domain.exception.ProductoNoDisponibleException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogoWebClientAdapterTest {

    private final AtomicInteger llamadas = new AtomicInteger();   // cuántas peticiones llegaron
    private ClientRequest ultimaPeticion;                           // la última petición enviada

    /** Crea el adaptador con un "catálogo falso" que siempre responde este estado y este JSON. */
    private CatalogoWebClientAdapter adapterQueResponde(HttpStatus status, String json) {
        ExchangeFunction catalogoFalso = peticion -> {
            llamadas.incrementAndGet();
            ultimaPeticion = peticion;
            return Mono.just(ClientResponse.create(status)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(json)
                    .build());
        };
        return new CatalogoWebClientAdapter(WebClient.builder().exchangeFunction(catalogoFalso), "http://catalogo");
    }

    @Test
    void obtener_producto_llama_a_la_url_correcta_y_traduce_el_json() {
        var adapter = adapterQueResponde(HttpStatus.OK,
                """
                {"id": 1, "nombre": "Teclado", "precio": 49.99, "stock": 10}
                """);

        StepVerifier.create(adapter.obtenerProducto(1L))
                .assertNext(producto -> {
                    assertThat(producto.nombre()).isEqualTo("Teclado");
                    assertThat(producto.precio()).isEqualByComparingTo("49.99");
                })
                .verifyComplete();

        assertThat(ultimaPeticion.method()).isEqualTo(HttpMethod.GET);
        assertThat(ultimaPeticion.url().getPath()).isEqualTo("/api/productos/1");
    }

    @Test
    void obtener_producto_inexistente_404_se_traduce_a_producto_no_disponible() {
        var adapter = adapterQueResponde(HttpStatus.NOT_FOUND, "{}");

        StepVerifier.create(adapter.obtenerProducto(99L))
                .expectError(ProductoNoDisponibleException.class)
                .verify();
    }

    @Test
    void obtener_producto_con_catalogo_caido_reintenta_y_acaba_en_catalogo_no_disponible() {
        var adapter = adapterQueResponde(HttpStatus.INTERNAL_SERVER_ERROR, "{}");

        StepVerifier.create(adapter.obtenerProducto(1L))
                .expectError(CatalogoNoDisponibleException.class)
                .verify();

        assertThat(llamadas.get()).isEqualTo(3);   // 1 intento + 2 reintentos
    }

    @Test
    void reducir_stock_sin_stock_409_se_traduce_a_producto_no_disponible() {
        var adapter = adapterQueResponde(HttpStatus.CONFLICT, "{}");

        StepVerifier.create(adapter.reducirStock(1L, 100))
                .expectError(ProductoNoDisponibleException.class)
                .verify();

        assertThat(ultimaPeticion.method()).isEqualTo(HttpMethod.POST);
        assertThat(ultimaPeticion.url().getPath()).isEqualTo("/api/productos/1/reducir-stock");
    }

    @Test
    void reducir_stock_con_catalogo_caido_NO_reintenta() {
        var adapter = adapterQueResponde(HttpStatus.INTERNAL_SERVER_ERROR, "{}");

        StepVerifier.create(adapter.reducirStock(1L, 2))
                .expectError(CatalogoNoDisponibleException.class)
                .verify();

        assertThat(llamadas.get()).isEqualTo(1);   // ¡solo 1! reducir stock no es idempotente
    }
    
    @Test
    void reenvia_a_catalogo_el_token_del_usuario_que_hace_el_pedido() {
        var adapter = adapterQueResponde(HttpStatus.OK,
                """
                {"id": 1, "nombre": "Teclado", "precio": 49.99, "stock": 10}
                """);
        Jwt tokenDeLuis = Jwt.withTokenValue("token-de-luis").header("alg", "RS256").subject("luis").build();

        StepVerifier.create(adapter.obtenerProducto(1L)
                        // simula que la petición llegó con el token de luis (el contexto de Reactor)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(
                                new JwtAuthenticationToken(tokenDeLuis))))
                .expectNextCount(1)
                .verifyComplete();

        assertThat(ultimaPeticion.headers().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer token-de-luis");
    }
}