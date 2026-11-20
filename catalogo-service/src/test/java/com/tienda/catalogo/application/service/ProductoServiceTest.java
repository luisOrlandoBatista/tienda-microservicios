package com.tienda.catalogo.application.service;

import com.tienda.catalogo.application.port.in.CrearProductoCommand;
import com.tienda.catalogo.application.port.out.ProductoRepositoryPort;
import com.tienda.catalogo.domain.exception.ProductoNoEncontradoException;
import com.tienda.catalogo.domain.model.Precio;
import com.tienda.catalogo.domain.model.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepository;   // puerto FALSO: no hay base de datos

    @InjectMocks
    private ProductoService productoService;             // la clase que probamos, con el mock dentro

    @Test
    void crear_guarda_el_producto_y_lo_devuelve_con_id() {
        var command = new CrearProductoCommand("Ratón", "Inalámbrico", new BigDecimal("19.90"), 10);
        // Simulamos que la base de datos asigna el id 1
        when(productoRepository.guardar(any(Producto.class))).thenAnswer(invocacion -> {
            Producto p = invocacion.getArgument(0);
            return Producto.reconstruir(1L, p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock());
        });

        Producto creado = productoService.crear(command);

        assertThat(creado.getId()).isEqualTo(1L);
        assertThat(creado.getNombre()).isEqualTo("Ratón");
    }

    @Test
    void obtener_un_producto_inexistente_lanza_error() {
        when(productoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                .isInstanceOf(ProductoNoEncontradoException.class);
    }

    @Test
    void reducir_stock_guarda_el_producto_con_el_stock_actualizado() {
        Producto teclado = Producto.reconstruir(1L, "Teclado", null, Precio.de("49.99"), 10);
        when(productoRepository.buscarPorId(1L)).thenReturn(Optional.of(teclado));
        when(productoRepository.guardar(teclado)).thenReturn(teclado);

        Producto resultado = productoService.reducirStock(1L, 3);

        assertThat(resultado.getStock()).isEqualTo(7);
        verify(productoRepository).guardar(teclado);
    }

    @Test
    void eliminar_un_producto_inexistente_no_borra_nada() {
        when(productoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.eliminar(99L))
                .isInstanceOf(ProductoNoEncontradoException.class);
        verify(productoRepository, never()).eliminar(any());
    }

    @Test
    void aumentar_stock_guarda_el_producto_con_el_stock_sumado() {
        Producto teclado = Producto.reconstruir(1L, "Teclado", null, Precio.de("49.99"), 10);
        when(productoRepository.buscarPorId(1L)).thenReturn(Optional.of(teclado));
        when(productoRepository.guardar(teclado)).thenReturn(teclado);

        Producto resultado = productoService.aumentarStock(1L, 5);

        assertThat(resultado.getStock()).isEqualTo(15);
    }
}