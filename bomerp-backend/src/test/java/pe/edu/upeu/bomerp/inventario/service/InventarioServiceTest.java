package pe.edu.upeu.bomerp.inventario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import pe.edu.upeu.bomerp.catalogo.producto.repository.ProductoRepository;
import pe.edu.upeu.bomerp.inventario.dto.DescuentoFefoRequest;
import pe.edu.upeu.bomerp.inventario.dto.DescuentoFefoResponse;
import pe.edu.upeu.bomerp.inventario.entity.EstadoLote;
import pe.edu.upeu.bomerp.inventario.entity.Lote;
import pe.edu.upeu.bomerp.inventario.repository.LoteRepository;
import pe.edu.upeu.bomerp.inventario.repository.MovimientoKardexRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private MovimientoKardexRepository kardexRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Producto producto;
    private Lote loteProximo;
    private Lote loteFresco;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Aceite de Coco Virgen 500ml");
        producto.setStock(30);

        LocalDate hoy = LocalDate.now();

        // Lote 1 vence en 15 días (prioridad FEFO)
        loteProximo = Lote.builder()
                .id(1L)
                .codigoLote("LOT-COCO-01")
                .productoId(10L)
                .productoNombre(producto.getNombre())
                .fechaFabricacion(hoy.minusMonths(3))
                .fechaVencimiento(hoy.plusDays(15))
                .stockInicial(new BigDecimal("10.00"))
                .stockActual(new BigDecimal("10.00"))
                .costoUnitario(new BigDecimal("30.00"))
                .estado(EstadoLote.PROXIMO_A_VENCER)
                .build();

        // Lote 2 vence en 180 días
        loteFresco = Lote.builder()
                .id(2L)
                .codigoLote("LOT-COCO-02")
                .productoId(10L)
                .productoNombre(producto.getNombre())
                .fechaFabricacion(hoy.minusWeeks(2))
                .fechaVencimiento(hoy.plusDays(180))
                .stockInicial(new BigDecimal("20.00"))
                .stockActual(new BigDecimal("20.00"))
                .costoUnitario(new BigDecimal("31.00"))
                .estado(EstadoLote.DISPONIBLE)
                .build();
    }

    @Test
    @DisplayName("FEFO: Debe consumir primero el lote más próximo a vencer cuando la cantidad es cubierta por éste")
    void fefo_consumoParcialLoteMasAntiguo() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(loteRepository.findLotesParaDespachoFEFO(eq(10L), any()))
                .thenReturn(List.of(loteProximo, loteFresco));

        DescuentoFefoRequest request = DescuentoFefoRequest.builder()
                .productoId(10L)
                .cantidad(new BigDecimal("6.00"))
                .motivo("VENTA_MOSTRADOR")
                .documentoReferencia("TK-001")
                .build();

        DescuentoFefoResponse response = inventarioService.descontarStockFEFO(request);

        assertThat(response.getCantidadTotalDescontada()).isEqualByComparingTo("6.00");
        assertThat(response.getLotesAfectados()).hasSize(1);
        assertThat(response.getLotesAfectados().get(0).getCodigoLote()).isEqualTo("LOT-COCO-01");
        assertThat(response.getLotesAfectados().get(0).getCantidadDescontada()).isEqualByComparingTo("6.00");
        assertThat(loteProximo.getStockActual()).isEqualByComparingTo("4.00");
        assertThat(loteProximo.getEstado()).isEqualTo(EstadoLote.PROXIMO_A_VENCER);
        assertThat(loteFresco.getStockActual()).isEqualByComparingTo("20.00"); // Sin cambios
        verify(kardexRepository).save(any());
    }

    @Test
    @DisplayName("FEFO: Debe agotar el primer lote y continuar descontando del siguiente en orden de caducidad")
    void fefo_consumoCascadaMultiplesLotes() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(loteRepository.findLotesParaDespachoFEFO(eq(10L), any()))
                .thenReturn(List.of(loteProximo, loteFresco));

        // Solicitamos 15 unidades: 10 de loteProximo (se agota) + 5 de loteFresco
        DescuentoFefoRequest request = DescuentoFefoRequest.builder()
                .productoId(10L)
                .cantidad(new BigDecimal("15.00"))
                .motivo("VENTA_MOSTRADOR")
                .documentoReferencia("TK-002")
                .build();

        DescuentoFefoResponse response = inventarioService.descontarStockFEFO(request);

        assertThat(response.getCantidadTotalDescontada()).isEqualByComparingTo("15.00");
        assertThat(response.getLotesAfectados()).hasSize(2);

        // Primer lote agotado
        assertThat(response.getLotesAfectados().get(0).getCodigoLote()).isEqualTo("LOT-COCO-01");
        assertThat(response.getLotesAfectados().get(0).getCantidadDescontada()).isEqualByComparingTo("10.00");
        assertThat(response.getLotesAfectados().get(0).isLoteAgotado()).isTrue();
        assertThat(loteProximo.getStockActual()).isEqualByComparingTo("0.00");
        assertThat(loteProximo.getEstado()).isEqualTo(EstadoLote.AGOTADO);

        // Segundo lote absorbe el remanente
        assertThat(response.getLotesAfectados().get(1).getCodigoLote()).isEqualTo("LOT-COCO-02");
        assertThat(response.getLotesAfectados().get(1).getCantidadDescontada()).isEqualByComparingTo("5.00");
        assertThat(loteFresco.getStockActual()).isEqualByComparingTo("15.00");
        assertThat(loteFresco.getEstado()).isEqualTo(EstadoLote.DISPONIBLE);
    }

    @Test
    @DisplayName("FEFO: Debe rechazar el despacho si la cantidad solicitada supera el stock vigente acumulado")
    void fefo_rechazarStockInsuficiente() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(loteRepository.findLotesParaDespachoFEFO(eq(10L), any()))
                .thenReturn(List.of(loteProximo, loteFresco)); // Total = 30

        DescuentoFefoRequest request = DescuentoFefoRequest.builder()
                .productoId(10L)
                .cantidad(new BigDecimal("35.00"))
                .motivo("VENTA_MOSTRADOR")
                .build();

        assertThatThrownBy(() -> inventarioService.descontarStockFEFO(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Stock insuficiente bajo regla FEFO");
    }
}
