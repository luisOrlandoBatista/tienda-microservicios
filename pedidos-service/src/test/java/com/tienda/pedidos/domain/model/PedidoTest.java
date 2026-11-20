package com.tienda.pedidos.domain.model;

import com.tienda.pedidos.domain.exception.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PedidoTest {

    private final LineaPedido teclado = new LineaPedido(1L, "Teclado", new BigDecimal("49.99"), 2);
    private final LineaPedido raton = new LineaPedido(2L, "Ratón", new BigDecimal("19.90"), 1);

    @Test
    void un_pedido_nuevo_nace_confirmado_y_calcula_su_total() {
        Pedido pedido = Pedido.crear("luis", List.of(teclado, raton));

        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CONFIRMADO);
        assertThat(pedido.getFechaCreacion()).isNotNull();
        assertThat(pedido.total()).isEqualByComparingTo("119.88");   // 49.99*2 + 19.90
    }

    @Test
    void no_permite_un_pedido_sin_lineas() {
        assertThatThrownBy(() -> Pedido.crear("luis", List.of()))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void no_permite_una_linea_con_cantidad_cero() {
        assertThatThrownBy(() -> new LineaPedido(1L, "Teclado", new BigDecimal("49.99"), 0))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    void cancelar_dos_veces_lanza_error() {
        Pedido pedido = Pedido.crear("luis", List.of(teclado));
        pedido.cancelar();

        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CANCELADO);
        assertThatThrownBy(pedido::cancelar).isInstanceOf(DatoInvalidoException.class);
    }
}