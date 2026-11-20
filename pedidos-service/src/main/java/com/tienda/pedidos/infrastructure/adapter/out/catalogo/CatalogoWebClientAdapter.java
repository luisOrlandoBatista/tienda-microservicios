package com.tienda.pedidos.infrastructure.adapter.out.catalogo;

import com.tienda.pedidos.application.port.out.CatalogoNoDisponibleException;
import com.tienda.pedidos.application.port.out.CatalogoPort;
import com.tienda.pedidos.application.port.out.ProductoCatalogo;
import com.tienda.pedidos.domain.exception.ProductoNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Component
public class CatalogoWebClientAdapter implements CatalogoPort {

    private static final Duration TIMEOUT = Duration.ofSeconds(3);

    private final WebClient webClient;

    public CatalogoWebClientAdapter(WebClient.Builder builder, @Value("${catalogo.url}") String catalogoUrl) {
        this.webClient = builder.baseUrl(catalogoUrl).build();
    }

    @Override
    public Mono<ProductoCatalogo> obtenerProducto(Long productoId) {
        return webClient.get()
                .uri("/api/productos/{id}", productoId)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        respuesta -> Mono.error(new ProductoNoDisponibleException(productoId, "no existe")))
                .bodyToMono(ProductoCatalogoResponse.class)
                .map(r -> new ProductoCatalogo(r.id(), r.nombre(), r.precio(), r.stock()))
                .timeout(TIMEOUT)
                .retryWhen(Retry.backoff(2, Duration.ofMillis(200)).filter(this::esErrorTecnico))
                .onErrorMap(this::esErrorTecnico,
                        ex -> new CatalogoNoDisponibleException("No se pudo consultar el catálogo", ex));
    }

    @Override
    public Mono<Void> reducirStock(Long productoId, int cantidad) {
        return webClient.post()
                .uri("/api/productos/{id}/reducir-stock", productoId)
                .bodyValue(Map.of("cantidad", cantidad))
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        respuesta -> Mono.error(new ProductoNoDisponibleException(productoId, "no existe")))
                .onStatus(status -> status.value() == 409,
                        respuesta -> Mono.error(new ProductoNoDisponibleException(productoId, "stock insuficiente")))
                .toBodilessEntity()
                .then()
                .timeout(TIMEOUT)
                // ⚠️ SIN retry: reducir stock NO es idempotente (ver explicación)
                .onErrorMap(this::esErrorTecnico,
                        ex -> new CatalogoNoDisponibleException("No se pudo reducir el stock en el catálogo", ex));
    }

    @Override
    public Mono<Void> aumentarStock(Long productoId, int cantidad) {
        return webClient.post()
                .uri("/api/productos/{id}/aumentar-stock", productoId)
                .bodyValue(Map.of("cantidad", cantidad))
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        respuesta -> Mono.error(new ProductoNoDisponibleException(productoId, "no existe")))
                .toBodilessEntity()
                .then()
                .timeout(TIMEOUT)
                // SIN retry: igual que reducir, aumentar NO es idempotente
                .onErrorMap(this::esErrorTecnico,
                        ex -> new CatalogoNoDisponibleException("No se pudo devolver el stock al catálogo", ex));
    }

    /** Error técnico = cualquier cosa que no sea una respuesta de negocio (caída, timeout, 5xx…). */
    private boolean esErrorTecnico(Throwable ex) {
        return !(ex instanceof ProductoNoDisponibleException);
    }
}