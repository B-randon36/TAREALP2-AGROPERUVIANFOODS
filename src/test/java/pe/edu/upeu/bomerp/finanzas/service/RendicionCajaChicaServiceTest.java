package pe.edu.upeu.bomerp.finanzas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoResponse;
import pe.edu.upeu.bomerp.catalogo.producto.service.ProductoService;
import pe.edu.upeu.bomerp.exception.SolvenciaInsuficienteException;
import pe.edu.upeu.bomerp.finanzas.dto.CajaChicaResponse;
import pe.edu.upeu.bomerp.finanzas.dto.DetalleRendicionRequest;
import pe.edu.upeu.bomerp.finanzas.dto.RendicionCajaChicaRequest;
import pe.edu.upeu.bomerp.finanzas.dto.RendicionCajaChicaResponse;
import pe.edu.upeu.bomerp.finanzas.entity.CajaChica;
import pe.edu.upeu.bomerp.finanzas.entity.DetalleRendicionCajaChica;
import pe.edu.upeu.bomerp.finanzas.entity.RendicionCajaChica;
import pe.edu.upeu.bomerp.finanzas.repository.CajaChicaRepository;
import pe.edu.upeu.bomerp.finanzas.repository.DetalleRendicionCajaChicaRepository;
import pe.edu.upeu.bomerp.finanzas.repository.RendicionCajaChicaRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class RendicionCajaChicaServiceTest {

    @Autowired
    private RendicionCajaChicaService rendicionService;

    @Autowired
    private CajaChicaRepository cajaChicaRepository;

    @Autowired
    private RendicionCajaChicaRepository rendicionRepository;

    @Autowired
    private DetalleRendicionCajaChicaRepository detalleRepository;

    @MockitoBean
    private ProductoService productoService;

    private Long cajaChicaId;

    @BeforeEach
    void setUp() {
        // Mock de comunicación inter-módulo con catalogo
        when(productoService.obtener(anyLong())).thenReturn(ProductoResponse.builder()
                .id(100L)
                .nombre("Útiles de Oficina")
                .precio(new BigDecimal("25.00"))
                .build());

        // Obtener o crear la caja chica inicial con saldo disponible S/ 1000.00
        CajaChica caja = cajaChicaRepository.findByCodigo("CC-001")
                .orElseGet(() -> cajaChicaRepository.save(CajaChica.builder()
                        .codigo("CC-001")
                        .responsable("Juan Pérez")
                        .limiteMaximo(new BigDecimal("2000.00"))
                        .saldoDisponible(new BigDecimal("1000.00"))
                        .build()));
        caja.setSaldoDisponible(new BigDecimal("1000.00"));
        cajaChicaRepository.save(caja);
        cajaChicaId = caja.getId();
    }

    @Nested
    @DisplayName("1. Cabecera-Detalle y Cálculo de Totales")
    class CalculoTotalesTests {

        @Test
        @DisplayName("Debe calcular correctamente los subtotales de detalle y el total acumulado en la cabecera")
        void debeCalcularTotalesCorrectamente() {
            // Arrange: 2 ítems en el detalle
            // Item 1: Cantidad 4 * S/ 50.00 = S/ 200.00
            // Item 2: Cantidad 2 * S/ 100.00 = S/ 200.00
            // Total esperado: S/ 400.00
            DetalleRendicionRequest item1 = DetalleRendicionRequest.builder()
                    .concepto("Hojas Bond A4")
                    .cantidad(4)
                    .precioUnitario(new BigDecimal("50.00"))
                    .productoId(100L) // Cruza módulo catalogo
                    .build();

            DetalleRendicionRequest item2 = DetalleRendicionRequest.builder()
                    .concepto("Cartuchos de Tinta")
                    .cantidad(2)
                    .precioUnitario(new BigDecimal("100.00"))
                    .build();

            RendicionCajaChicaRequest request = RendicionCajaChicaRequest.builder()
                    .codigo("REND-2026-01")
                    .responsable("Carlos M.")
                    .cajaChicaId(cajaChicaId)
                    .detalles(List.of(item1, item2))
                    .build();

            // Act
            RendicionCajaChicaResponse response = rendicionService.registrarRendicion(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getMontoTotal()).isEqualByComparingTo(new BigDecimal("400.00"));
            assertThat(response.getDetalles()).hasSize(2);
            assertThat(response.getDetalles().get(0).getSubtotal()).isEqualByComparingTo(new BigDecimal("200.00"));
            assertThat(response.getDetalles().get(1).getSubtotal()).isEqualByComparingTo(new BigDecimal("200.00"));
        }
    }

    @Nested
    @DisplayName("2. Regla de Negocio RN-FIN-01 (Solvencia de Caja Chica / Control de Liquidez)")
    class ReglaSolvenciaTests {

        @Test
        @DisplayName("RN-FIN-01: Debe permitir el registro si el monto total (S/ 600.00) es menor o igual al saldo disponible (S/ 1000.00)")
        void debePermitirRegistroCuandoHaySolvencia() {
            DetalleRendicionRequest item = DetalleRendicionRequest.builder()
                    .concepto("Mantenimiento de Equipo")
                    .cantidad(3)
                    .precioUnitario(new BigDecimal("200.00")) // Total = 600.00
                    .build();

            RendicionCajaChicaRequest request = RendicionCajaChicaRequest.builder()
                    .codigo("REND-2026-02")
                    .responsable("Ana Torres")
                    .cajaChicaId(cajaChicaId)
                    .detalles(List.of(item))
                    .build();

            RendicionCajaChicaResponse response = rendicionService.registrarRendicion(request);

            assertThat(response.getMontoTotal()).isEqualByComparingTo(new BigDecimal("600.00"));

            CajaChica cajaActualizada = cajaChicaRepository.findById(cajaChicaId).orElseThrow();
            // Saldo disponible disminuye de 1000.00 a 400.00
            assertThat(cajaActualizada.getSaldoDisponible()).isEqualByComparingTo(new BigDecimal("400.00"));
        }

        @Test
        @DisplayName("RN-FIN-01: Debe lanzar SolvenciaInsuficienteException si el monto total (S/ 1500.00) supera el saldo disponible (S/ 1000.00)")
        void debeLanzarExcepcionCuandoMontoExcedeSaldoDisponible() {
            DetalleRendicionRequest item = DetalleRendicionRequest.builder()
                    .concepto("Compra de Laptop Repuesto")
                    .cantidad(1)
                    .precioUnitario(new BigDecimal("1500.00")) // Total = 1500.00 > 1000.00
                    .build();

            RendicionCajaChicaRequest request = RendicionCajaChicaRequest.builder()
                    .codigo("REND-EXCESO-01")
                    .responsable("Ana Torres")
                    .cajaChicaId(cajaChicaId)
                    .detalles(List.of(item))
                    .build();

            assertThatThrownBy(() -> rendicionService.registrarRendicion(request))
                    .isInstanceOf(SolvenciaInsuficienteException.class)
                    .hasMessageContaining("RN-FIN-01")
                    .hasMessageContaining("supera la solvencia/saldo disponible");
        }
    }

    @Nested
    @DisplayName("3. Transacción Atómica (Caso Éxito y Rollback)")
    class TransaccionAtomicaTests {

        @Test
        @DisplayName("Caso Éxito: Cabecera y detalles se persisten atómicamente y el saldo de la caja se actualiza")
        void casoExitoPersistenciaAtomica() {
            // Estado antes
            long rendicionesAntes = rendicionRepository.count();
            long detallesAntes = detalleRepository.count();
            CajaChica cajaAntes = cajaChicaRepository.findById(cajaChicaId).orElseThrow();
            BigDecimal saldoAntes = cajaAntes.getSaldoDisponible();

            assertThat(saldoAntes).isEqualByComparingTo(new BigDecimal("1000.00"));

            // Operación: Registrar rendición por S/ 300.00
            DetalleRendicionRequest d1 = DetalleRendicionRequest.builder()
                    .concepto("Pasajes urbanos")
                    .cantidad(5)
                    .precioUnitario(new BigDecimal("20.00")) // S/ 100.00
                    .build();

            DetalleRendicionRequest d2 = DetalleRendicionRequest.builder()
                    .concepto("Almuerzo de trabajo")
                    .cantidad(2)
                    .precioUnitario(new BigDecimal("100.00")) // S/ 200.00
                    .build();

            RendicionCajaChicaRequest request = RendicionCajaChicaRequest.builder()
                    .codigo("REND-EXITO-01")
                    .responsable("Pedro Picapiedra")
                    .cajaChicaId(cajaChicaId)
                    .detalles(List.of(d1, d2))
                    .build();

            RendicionCajaChicaResponse resp = rendicionService.registrarRendicion(request);

            // Estado después
            assertThat(rendicionRepository.count()).isEqualTo(rendicionesAntes + 1);
            assertThat(detalleRepository.count()).isEqualTo(detallesAntes + 2);

            CajaChica cajaDespues = cajaChicaRepository.findById(cajaChicaId).orElseThrow();
            assertThat(cajaDespues.getSaldoDisponible()).isEqualByComparingTo(new BigDecimal("700.00"));

            RendicionCajaChica rendicionBd = rendicionRepository.findById(resp.getId()).orElseThrow();
            assertThat(rendicionBd.getMontoTotal()).isEqualByComparingTo(new BigDecimal("300.00"));
            assertThat(rendicionBd.getDetalles()).hasSize(2);
        }

        @Test
        @DisplayName("Caso Rollback: Si falla por la regla RN-FIN-01, se revierte toda la operación (ni cabecera ni detalles persisten)")
        void casoRollbackTransaccional() {
            // Estado antes
            long rendicionesAntes = rendicionRepository.count();
            long detallesAntes = detalleRepository.count();
            BigDecimal saldoAntes = cajaChicaRepository.findById(cajaChicaId).orElseThrow().getSaldoDisponible();

            // Operación que viola la regla RN-FIN-01 (Monto S/ 1200.00 > Saldo S/ 1000.00)
            DetalleRendicionRequest d1 = DetalleRendicionRequest.builder()
                    .concepto("Gasto excesivo item 1")
                    .cantidad(1)
                    .precioUnitario(new BigDecimal("600.00"))
                    .build();

            DetalleRendicionRequest d2 = DetalleRendicionRequest.builder()
                    .concepto("Gasto excesivo item 2")
                    .cantidad(1)
                    .precioUnitario(new BigDecimal("600.00"))
                    .build();

            RendicionCajaChicaRequest request = RendicionCajaChicaRequest.builder()
                    .codigo("REND-FAIL-01")
                    .responsable("Pedro Picapiedra")
                    .cajaChicaId(cajaChicaId)
                    .detalles(List.of(d1, d2))
                    .build();

            assertThatThrownBy(() -> rendicionService.registrarRendicion(request))
                    .isInstanceOf(SolvenciaInsuficienteException.class);

            // Estado después: Estado intocado (Rollback verificado)
            assertThat(rendicionRepository.count()).isEqualTo(rendicionesAntes);
            assertThat(detalleRepository.count()).isEqualTo(detallesAntes);
            BigDecimal saldoDespues = cajaChicaRepository.findById(cajaChicaId).orElseThrow().getSaldoDisponible();
            assertThat(saldoDespues).isEqualByComparingTo(saldoAntes);
        }
    }
}
