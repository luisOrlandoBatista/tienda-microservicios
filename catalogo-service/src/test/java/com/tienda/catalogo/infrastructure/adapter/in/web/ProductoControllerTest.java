package com.tienda.catalogo.infrastructure.adapter.in.web;

import com.tienda.catalogo.application.port.in.ActualizarProductoUseCase;
import com.tienda.catalogo.application.port.in.AumentarStockUseCase;
import com.tienda.catalogo.application.port.in.ConsultarProductoUseCase;
import com.tienda.catalogo.application.port.in.CrearProductoCommand;
import com.tienda.catalogo.application.port.in.CrearProductoUseCase;
import com.tienda.catalogo.application.port.in.EliminarProductoUseCase;
import com.tienda.catalogo.application.port.in.ReducirStockUseCase;
import com.tienda.catalogo.domain.exception.ProductoNoEncontradoException;
import com.tienda.catalogo.domain.exception.StockInsuficienteException;
import com.tienda.catalogo.domain.model.Precio;
import com.tienda.catalogo.domain.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;   // simula peticiones HTTP sin arrancar un servidor real

    // Mocks de los casos de uso: el controlador los necesita para arrancar
    @MockitoBean private CrearProductoUseCase crearProductoUseCase;
    @MockitoBean private ConsultarProductoUseCase consultarProductoUseCase;
    @MockitoBean private ActualizarProductoUseCase actualizarProductoUseCase;
    @MockitoBean private EliminarProductoUseCase eliminarProductoUseCase;
    @MockitoBean private ReducirStockUseCase reducirStockUseCase;
    @MockitoBean private AumentarStockUseCase aumentarStockUseCase;

    @Test
    void crear_devuelve_201_con_la_cabecera_location() throws Exception {
        when(crearProductoUseCase.crear(any(CrearProductoCommand.class)))
                .thenReturn(Producto.reconstruir(1L, "Teclado", null, Precio.de("49.99"), 10));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Teclado", "precio": 49.99, "stock": 10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/productos/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Teclado"));
    }

    @Test
    void obtener_un_producto_inexistente_devuelve_404() throws Exception {
        when(consultarProductoUseCase.obtenerPorId(99L)).thenThrow(new ProductoNoEncontradoException(99L));

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Producto no encontrado"));
    }

    @Test
    void crear_con_datos_invalidos_devuelve_400_con_los_campos_erroneos() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "", "precio": -5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.precio").exists())
                .andExpect(jsonPath("$.errores.stock").exists());
    }

    @Test
    void reducir_mas_stock_del_disponible_devuelve_409() throws Exception {
        when(reducirStockUseCase.reducirStock(1L, 100))
                .thenThrow(new StockInsuficienteException(1L, 10, 100));

        mockMvc.perform(post("/api/productos/1/reducir-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cantidad": 100}
                                """))
                .andExpect(status().isConflict());
    }
}