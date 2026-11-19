package com.tienda.catalogo.infrastructure.adapter.out.persistence;

import com.tienda.catalogo.TestcontainersConfiguration;
import com.tienda.catalogo.domain.model.Precio;
import com.tienda.catalogo.domain.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class ProductoPersistenceAdapterTest {

    @Autowired
    private ProductoPersistenceAdapter adapter;

    @Test
    void guardar_un_producto_nuevo_le_asigna_id() {
        Producto nuevo = Producto.crear("Monitor", "27 pulgadas", Precio.de("199.99"), 4);

        Producto guardado = adapter.guardar(nuevo);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Monitor");
        assertThat(guardado.getPrecio().valor()).isEqualByComparingTo("199.99");
    }

    @Test
    void buscar_por_id_devuelve_el_producto_guardado() {
        Producto guardado = adapter.guardar(Producto.crear("Monitor", null, Precio.de("199.99"), 4));

        Optional<Producto> encontrado = adapter.buscarPorId(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getStock()).isEqualTo(4);
    }

    @Test
    void guardar_un_producto_existente_actualiza_sus_datos() {
        Producto guardado = adapter.guardar(Producto.crear("Monitor", null, Precio.de("199.99"), 4));

        guardado.reducirStock(1);
        guardado.actualizarDatos("Monitor 4K", "Nuevo modelo", Precio.de("249.00"));
        adapter.guardar(guardado);

        Producto actualizado = adapter.buscarPorId(guardado.getId()).orElseThrow();
        assertThat(actualizado.getNombre()).isEqualTo("Monitor 4K");
        assertThat(actualizado.getStock()).isEqualTo(3);
    }

    @Test
    void eliminar_borra_el_producto() {
        Producto guardado = adapter.guardar(Producto.crear("Monitor", null, Precio.de("199.99"), 4));

        adapter.eliminar(guardado.getId());

        assertThat(adapter.buscarPorId(guardado.getId())).isEmpty();
    }
}