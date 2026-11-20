package com.tienda.pedidos.infrastructure.adapter.out.persistence;

import com.tienda.pedidos.TestcontainersConfiguration;
import com.tienda.pedidos.domain.model.EstadoPedido;
import com.tienda.pedidos.domain.model.LineaPedido;
import com.tienda.pedidos.domain.model.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class PedidoPersistenceAdapterTest {

    @Autowired
    private PedidoPersistenceAdapter adapter;

    private Pedido nuevoPedido() {
        return Pedido.crear("luis", List.of(
                new LineaPedido(1L, "Teclado", new BigDecimal("49.99"), 2),
                new LineaPedido(2L, "Ratón", new BigDecimal("19.90"), 1)));
    }

    @Test
    void guardar_un_pedido_nuevo_guarda_la_cabecera_y_sus_lineas() {
        StepVerifier.create(adapter.guardar(nuevoPedido()))
                .assertNext(guardado -> {
                    assertThat(guardado.getId()).isNotNull();
                    assertThat(guardado.getLineas()).hasSize(2);
                    assertThat(guardado.total()).isEqualByComparingTo("119.88");
                })
                .verifyComplete();
    }

    @Test
    void buscar_por_id_devuelve_el_pedido_con_sus_lineas() {
        Mono<Pedido> flujo = adapter.guardar(nuevoPedido())
                .flatMap(guardado -> adapter.buscarPorId(guardado.getId()));

        StepVerifier.create(flujo)
                .assertNext(leido -> {
                    assertThat(leido.getLineas()).hasSize(2);
                    assertThat(leido.getEstado()).isEqualTo(EstadoPedido.CONFIRMADO);
                })
                .verifyComplete();
    }

    @Test
    void guardar_un_pedido_existente_actualiza_el_estado_sin_duplicar_lineas() {
        Mono<Pedido> flujo = adapter.guardar(nuevoPedido())
                .map(guardado -> {
                    guardado.cancelar();
                    return guardado;
                })
                .flatMap(adapter::guardar)
                .flatMap(actualizado -> adapter.buscarPorId(actualizado.getId()));

        StepVerifier.create(flujo)
                .assertNext(leido -> {
                    assertThat(leido.getEstado()).isEqualTo(EstadoPedido.CANCELADO);
                    assertThat(leido.getLineas()).hasSize(2);   // ¡no se han duplicado!
                })
                .verifyComplete();
    }

    @Test
    void buscar_un_id_inexistente_devuelve_un_mono_vacio() {
        StepVerifier.create(adapter.buscarPorId(999_999L))
                .verifyComplete();      // termina SIN emitir nada = Mono vacío
    }
}