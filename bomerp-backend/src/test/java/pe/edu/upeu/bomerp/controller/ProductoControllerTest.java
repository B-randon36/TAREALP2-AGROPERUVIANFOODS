package pe.edu.upeu.bomerp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.bomerp.dto.request.ProductoRequest;
import pe.edu.upeu.bomerp.dto.response.ProductoResponse;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import pe.edu.upeu.bomerp.exception.TraceIdFilter;
import pe.edu.upeu.bomerp.service.ProductoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@Import(TraceIdFilter.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProductoRequest validRequest;
    private ProductoResponse validResponse;

    @BeforeEach
    void setUp() {
        validRequest = ProductoRequest.builder()
                .nombre("Arándano Orgánico")
                .descripcion("Caja de arándanos orgánicos premium de Agro Peruvian Foods")
                .codigo("PROD-ARAN-001")
                .precio(new BigDecimal("15.50"))
                .stock(100)
                .activo(true)
                .build();

        validResponse = ProductoResponse.builder()
                .id(1L)
                .nombre("Arándano Orgánico")
                .descripcion("Caja de arándanos orgánicos premium de Agro Peruvian Foods")
                .codigo("PROD-ARAN-001")
                .precio(new BigDecimal("15.50"))
                .stock(100)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    @Test
    void crearProducto_DebeRetornar201() throws Exception {
        Mockito.when(productoService.crear(any(ProductoRequest.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Arándano Orgánico")))
                .andExpect(jsonPath("$.codigo", is("PROD-ARAN-001")))
                .andExpect(jsonPath("$.precio", is(15.50)));
    }

    @Test
    void crearProducto_ConCamposInvalidos_DebeRetornar400() throws Exception {
        ProductoRequest invalidRequest = ProductoRequest.builder()
                .nombre("") // Blanco (invalido)
                .codigo("PROD-ARAN-001")
                .precio(new BigDecimal("-5.00")) // Negativo (invalido)
                .stock(-10) // Negativo (invalido)
                .build();

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.traceId").exists());
    }

    @Test
    void obtenerPorId_Existente_DebeRetornar200() throws Exception {
        Mockito.when(productoService.obtenerPorId(1L)).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/productos/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Arándano Orgánico")));
    }

    @Test
    void obtenerPorId_Inexistente_DebeRetornar404() throws Exception {
        Mockito.when(productoService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con el ID: 99"));

        mockMvc.perform(get("/api/v1/productos/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Producto no encontrado con el ID: 99")))
                .andExpect(jsonPath("$.traceId").exists());
    }

    @Test
    void listarTodos_DebeRetornar200() throws Exception {
        List<ProductoResponse> list = Collections.singletonList(validResponse);
        Mockito.when(productoService.listarTodos()).thenReturn(list);

        mockMvc.perform(get("/api/v1/productos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Arándano Orgánico")));
    }

    @Test
    void actualizarProducto_DebeRetornar200() throws Exception {
        Mockito.when(productoService.actualizar(eq(1L), any(ProductoRequest.class))).thenReturn(validResponse);

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Arándano Orgánico")));
    }

    @Test
    void eliminarProducto_Existente_DebeRetornar204() throws Exception {
        doNothing().when(productoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarProducto_Inexistente_DebeRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Producto no encontrado con el ID: 99"))
                .when(productoService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.traceId").exists());
    }
}
