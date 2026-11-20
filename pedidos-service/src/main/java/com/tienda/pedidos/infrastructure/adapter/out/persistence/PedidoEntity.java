package com.tienda.pedidos.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("pedidos")
public record PedidoEntity(
        @Id Long id,
        String cliente,
        String estado,
        BigDecimal total,
        @Column("fecha_creacion") Instant fechaCreacion) {
}