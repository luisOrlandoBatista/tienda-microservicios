package com.tienda.catalogo.domain.model;

import com.tienda.catalogo.domain.exception.DatoInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Precio(BigDecimal valor) {

    public Precio {
        if (valor == null) {
            throw new DatoInvalidoException("El precio es obligatorio");
        }
        if (valor.signum() <= 0) {
            throw new DatoInvalidoException("El precio debe ser mayor que cero");
        }
        valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static Precio de(String valor) {
        return new Precio(new BigDecimal(valor));
    }
}