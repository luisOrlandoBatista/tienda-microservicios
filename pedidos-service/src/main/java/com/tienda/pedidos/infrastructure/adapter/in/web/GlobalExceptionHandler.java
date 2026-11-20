package com.tienda.pedidos.infrastructure.adapter.in.web;

import com.tienda.pedidos.application.port.out.CatalogoNoDisponibleException;
import com.tienda.pedidos.domain.exception.DominioException;
import com.tienda.pedidos.domain.exception.PedidoNoEncontradoException;
import com.tienda.pedidos.domain.exception.ProductoNoDisponibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ProblemDetail manejarNoEncontrado(PedidoNoEncontradoException ex) {
        return problema(HttpStatus.NOT_FOUND, "Pedido no encontrado", ex.getMessage());
    }

    @ExceptionHandler(ProductoNoDisponibleException.class)
    public ProblemDetail manejarProductoNoDisponible(ProductoNoDisponibleException ex) {
        return problema(HttpStatus.CONFLICT, "Producto no disponible", ex.getMessage());
    }

    @ExceptionHandler(DominioException.class)
    public ProblemDetail manejarReglaDeNegocio(DominioException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Regla de negocio incumplida", ex.getMessage());
    }

    @ExceptionHandler(CatalogoNoDisponibleException.class)
    public ProblemDetail manejarCatalogoNoDisponible(CatalogoNoDisponibleException ex) {
        log.warn("Catálogo no disponible: {}", ex.getMessage(), ex);
        return problema(HttpStatus.SERVICE_UNAVAILABLE, "Servicio no disponible",
                "El catálogo no está disponible en este momento. Inténtalo más tarde.");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail manejarErrorInesperado(Exception ex) {
        log.error("Error inesperado", ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", "Ha ocurrido un error inesperado");
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleWebExchangeBindException(WebExchangeBindException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          ServerWebExchange exchange) {
        ProblemDetail problema = ex.getBody();
        problema.setTitle("Datos de entrada no válidos");
        problema.setDetail("Uno o más campos no son correctos");

        Map<String, String> errores = new LinkedHashMap<>();
        ex.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
        problema.setProperty("errores", errores);

        return handleExceptionInternal(ex, problema, headers, status, exchange);
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalle) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalle);
        problema.setTitle(titulo);
        return problema;
    }
}