package com.tienda.pedidos.domain.model;

import com.tienda.pedidos.domain.exception.DatoInvalidoException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Pedido {

    private final Long id;
    private final String cliente;
    private final List<LineaPedido> lineas;
    private final Instant fechaCreacion;
    private EstadoPedido estado;

    private Pedido(Long id, String cliente, List<LineaPedido> lineas, EstadoPedido estado, Instant fechaCreacion) {
        if (cliente == null || cliente.isBlank()) {
            throw new DatoInvalidoException("El cliente es obligatorio");
        }
        if (lineas == null || lineas.isEmpty()) {
            throw new DatoInvalidoException("Un pedido debe tener al menos una línea");
        }
        this.id = id;
        this.cliente = cliente;
        this.lineas = List.copyOf(lineas);      // copia inmutable: nadie puede añadir líneas desde fuera
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    /** Pedido NUEVO: nace confirmado y con la fecha actual. */
    public static Pedido crear(String cliente, List<LineaPedido> lineas) {
        return new Pedido(null, cliente, lineas, EstadoPedido.CONFIRMADO, Instant.now());
    }

    /** Pedido que YA EXISTE (leído de la base de datos). */
    public static Pedido reconstruir(Long id, String cliente, List<LineaPedido> lineas,
                                     EstadoPedido estado, Instant fechaCreacion) {
        return new Pedido(id, cliente, lineas, estado, fechaCreacion);
    }

    public BigDecimal total() {
        return lineas.stream()
                .map(LineaPedido::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void cancelar() {
        if (estado == EstadoPedido.CANCELADO) {
            throw new DatoInvalidoException("El pedido ya está cancelado");
        }
        estado = EstadoPedido.CANCELADO;
    }

    public Long getId() { return id; }
    public String getCliente() { return cliente; }
    public List<LineaPedido> getLineas() { return lineas; }
    public EstadoPedido getEstado() { return estado; }
    public Instant getFechaCreacion() { return fechaCreacion; }
}