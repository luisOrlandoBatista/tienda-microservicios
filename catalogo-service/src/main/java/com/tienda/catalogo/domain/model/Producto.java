package com.tienda.catalogo.domain.model;

import com.tienda.catalogo.domain.exception.DatoInvalidoException;
import com.tienda.catalogo.domain.exception.StockInsuficienteException;

public class Producto {

    private final Long id;
    private String nombre;
    private String descripcion;
    private Precio precio;
    private int stock;

    private Producto(Long id, String nombre, String descripcion, Precio precio, int stock) {
        this.id = id;
        this.nombre = validarNombre(nombre);
        this.descripcion = descripcion;
        this.precio = validarPrecio(precio);
        this.stock = validarStockInicial(stock);
    }

    /** Para un producto NUEVO (todavía sin id: lo asignará la base de datos). */
    public static Producto crear(String nombre, String descripcion, Precio precio, int stock) {
        return new Producto(null, nombre, descripcion, precio, stock);
    }

    /** Para un producto que YA EXISTE (por ejemplo, leído de la base de datos). */
    public static Producto reconstruir(Long id, String nombre, String descripcion, Precio precio, int stock) {
        return new Producto(id, nombre, descripcion, precio, stock);
    }

    // ---------- Comportamiento de negocio ----------

    public void actualizarDatos(String nombre, String descripcion, Precio precio) {
        this.nombre = validarNombre(nombre);
        this.descripcion = descripcion;
        this.precio = validarPrecio(precio);
    }

    public void reducirStock(int cantidad) {
        validarCantidad(cantidad);
        if (cantidad > stock) {
            throw new StockInsuficienteException(id, stock, cantidad);
        }
        stock -= cantidad;
    }

    public void aumentarStock(int cantidad) {
        validarCantidad(cantidad);
        stock += cantidad;
    }

    // ---------- Reglas de validación ----------

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre es obligatorio");
        }
        if (nombre.trim().length() > 100) {
            throw new DatoInvalidoException("El nombre no puede superar los 100 caracteres");
        }
        return nombre.trim();
    }

    private static Precio validarPrecio(Precio precio) {
        if (precio == null) {
            throw new DatoInvalidoException("El precio es obligatorio");
        }
        return precio;
    }

    private static int validarStockInicial(int stock) {
        if (stock < 0) {
            throw new DatoInvalidoException("El stock no puede ser negativo");
        }
        return stock;
    }

    private static void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new DatoInvalidoException("La cantidad debe ser mayor que cero");
        }
    }

    // ---------- Getters (sin setters) ----------

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Precio getPrecio() { return precio; }
    public int getStock() { return stock; }
}