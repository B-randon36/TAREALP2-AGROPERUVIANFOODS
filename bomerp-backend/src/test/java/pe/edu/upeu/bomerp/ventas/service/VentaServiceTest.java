package pe.edu.upeu.bomerp.ventas.service;

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
import pe.edu.upeu.bomerp.finanzas.dto.MovimientoCajaRequest;
import pe.edu.upeu.bomerp.finanzas.dto.SesionCajaResponse;
import pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera;
import pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero;
import pe.edu.upeu.bomerp.finanzas.repository.CategoriaFinancieraRepository;
import pe.edu.upeu.bomerp.finanzas.service.SesionCajaService;
import pe.edu.upeu.bomerp.inventario.dto.DescuentoFefoRequest;
import pe.edu.upeu.bomerp.inventario.dto.DescuentoFefoResponse;
import pe.edu.upeu.bomerp.inventario.dto.LoteDescontadoItem;
import pe.edu.upeu.bomerp.inventario.service.InventarioService;
import pe.edu.upeu.bomerp.ventas.dto.VentaCreateRequest;
import pe.edu.upeu.bomerp.ventas.dto.VentaItemRequest;
import pe.edu.upeu.bomerp.ventas.dto.VentaResponse;
import pe.edu.upeu.bomerp.ventas.entity.CanalVenta;
import pe.edu.upeu.bomerp.ventas.entity.EstadoVenta;
import pe.edu.upeu.bomerp.ventas.entity.MetodoPagoVenta;
import pe.edu.upeu.bomerp.ventas.entity.Venta;
import pe.edu.upeu.bomerp.ventas.repository.VentaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private InventarioService inventarioService;

    @Mock
    private SesionCajaService sesionCajaService;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaFinancieraRepository categoriaFinancieraRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Producto producto;
    private SesionCajaResponse sesionActiva;
    private CategoriaFinanciera categoriaIngreso;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(4L);
        producto.setNombre("Aceite de Coco Virgen 500ml");
        producto.setStock(30);

        sesionActiva = SesionCajaResponse.builder()
                .id(100L)
                .codigoSesion("SES-20261019-01")
                .cajero("Brandon Ccalla")
                .estado(pe.edu.upeu.bomerp.finanzas.entity.EstadoCaja.ABIERTA)
                .saldoInicial(new BigDecimal("300.00"))
                .saldoEsperadoEfectivo(new BigDecimal("300.00"))
                .build();

        categoriaIngreso = new CategoriaFinanciera();
        categoriaIngreso.setId(1L);
        categoriaIngreso.setNombre("Venta de Mercaderías");
        categoriaIngreso.setTipoDefault(TipoFinanciero.INGRESO);
    }

    @Test
    @DisplayName("Venta POS Efectivo: Debe descontar stock FEFO, calcular vuelto y registrar INGRESO en SesionCaja")
    void ventaEfectivo_completaConFefoYSincronizacionCaja() {
        when(sesionCajaService.obtenerUltimaSesionActiva()).thenReturn(sesionActiva);
        when(productoRepository.findById(4L)).thenReturn(Optional.of(producto));
        when(categoriaFinancieraRepository.findAll()).thenReturn(List.of(categoriaIngreso));

        // Mock de respuesta FEFO
        DescuentoFefoResponse fefoRes = DescuentoFefoResponse.builder()
                .productoId(4L)
                .productoNombre(producto.getNombre())
                .cantidadTotalSolicitada(new BigDecimal("2.00"))
                .cantidadTotalDescontada(new BigDecimal("2.00"))
                .stockTotalRestante(new BigDecimal("28.00"))
                .lotesAfectados(List.of(LoteDescontadoItem.builder()
                        .loteId(1L)
                        .codigoLote("LOT-COCO-A1")
                        .fechaVencimiento(LocalDate.now().plusDays(15))
                        .cantidadDescontada(new BigDecimal("2.00"))
                        .stockRestanteLote(new BigDecimal("8.00"))
                        .loteAgotado(false)
                        .build()))
                .build();
        when(inventarioService.descontarStockFEFO(any(DescuentoFefoRequest.class))).thenReturn(fefoRes);

        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> {
            Venta v = inv.getArgument(0);
            v.setId(500L);
            return v;
        });

        // Solicitamos 2 unidades a S/ 48.00 c/u = Total S/ 96.00. Cliente paga con S/ 100.00 en efectivo
        VentaCreateRequest request = VentaCreateRequest.builder()
                .canal(CanalVenta.TIENDA_MOSTRADOR)
                .clienteTipo("CONSUMIDOR_FINAL")
                .clienteNombre("Público General")
                .metodoPago(MetodoPagoVenta.EFECTIVO)
                .montoPagado(new BigDecimal("100.00"))
                .cajeroNombre("Brandon Ccalla")
                .items(List.of(VentaItemRequest.builder()
                        .productoId(4L)
                        .cantidad(new BigDecimal("2.00"))
                        .precioUnitario(new BigDecimal("48.00"))
                        .build()))
                .build();

        VentaResponse response = ventaService.registrarVenta(request);

        assertThat(response.getId()).isEqualTo(500L);
        assertThat(response.getTotal()).isEqualByComparingTo("96.00");
        assertThat(response.getMontoPagado()).isEqualByComparingTo("100.00");
        assertThat(response.getVuelto()).isEqualByComparingTo("4.00"); // 100 - 96
        assertThat(response.getEstado()).isEqualTo(EstadoVenta.EMITIDA);
        assertThat(response.getDetalles()).hasSize(1);
        assertThat(response.getDetalles().get(0).getLotesConsumidosFefo()).contains("LOT-COCO-A1 (2.00 un.)");

        // Verifica que se invocó FEFO
        verify(inventarioService).descontarStockFEFO(any(DescuentoFefoRequest.class));

        // Verifica que se sincronizó la gaveta de efectivo con el monto total de la venta
        verify(sesionCajaService).registrarMovimiento(eq(100L), any(MovimientoCajaRequest.class));
    }

    @Test
    @DisplayName("Venta POS Efectivo: Debe rechazar la venta si no existe turno de caja abierto")
    void ventaEfectivo_sinCajaAbierta_debeRechazar() {
        when(sesionCajaService.obtenerUltimaSesionActiva()).thenReturn(null);

        VentaCreateRequest request = VentaCreateRequest.builder()
                .canal(CanalVenta.TIENDA_MOSTRADOR)
                .metodoPago(MetodoPagoVenta.EFECTIVO)
                .montoPagado(new BigDecimal("100.00"))
                .items(List.of(VentaItemRequest.builder()
                        .productoId(4L)
                        .cantidad(new BigDecimal("1.00"))
                        .precioUnitario(new BigDecimal("48.00"))
                        .build()))
                .build();

        assertThatThrownBy(() -> ventaService.registrarVenta(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("RN-FIN-02: Se requiere una sesión de caja abierta");
    }
}
