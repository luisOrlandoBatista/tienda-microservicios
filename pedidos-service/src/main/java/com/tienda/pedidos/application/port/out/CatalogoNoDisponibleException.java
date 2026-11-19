package com.tienda.pedidos.application.port.out;

public class CatalogoNoDisponibleException extends RuntimeException {

    public CatalogoNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}