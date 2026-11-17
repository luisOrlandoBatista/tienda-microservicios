package com.tienda.catalogo.domain.model;

import com.tienda.catalogo.domain.exception.DatoInvalidoException;
import com.tienda.catalogo.domain.exception.StockInsuficienteException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductoTest {

    private Producto productoConStock(int stock) {
        return Producto.reconstruir(1L, "Teclado", "Teclado mecánico", Precio.de("49.99"), stock);
    }

    @Test
    void crear_producto_valido() {
        Producto producto = Producto.crear("  Ratón  ", "Inalámbrico", Precio.de("19.9"), 10);

        assertThat(producto.getId()).isNull();
        assertThat(producto.getNombre()).isEqualTo("Ratón");
        assertThat(producto.getPrecio().valor()).isEqualByComparingTo("19.90");
        assertThat(producto.getStock()).isEqualTo(10);
    }

    @Test
    void no_permite_nombre_vacio() {
        assertThatThrownBy(() -> Producto.crear("   ", null, Precio.de("10"), 1))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    void no_permite_stock_negativo() {
        assertThatThrownBy(() -> Producto.crear("Ratón", null, Precio.de("10"), -1))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void no_permite_precio_cero() {
        assertThatThrownBy(() -> Precio.de("0"))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void reducir_stock_resta_la_cantidad() {
        Producto producto = productoConStock(10);

        producto.reducirStock(3);

        assertThat(producto.getStock()).isEqualTo(7);
    }

    @Test
    void reducir_mas_stock_del_disponible_lanza_error_y_no_cambia_el_stock() {
        Producto producto = productoConStock(2);

        assertThatThrownBy(() -> producto.reducirStock(5))
                .isInstanceOf(StockInsuficienteException.class);
        assertThat(producto.getStock()).isEqualTo(2);
    }

    @Test
    void aumentar_stock_suma_la_cantidad() {
        Producto producto = productoConStock(2);

        producto.aumentarStock(8);

        assertThat(producto.getStock()).isEqualTo(10);
    }
}