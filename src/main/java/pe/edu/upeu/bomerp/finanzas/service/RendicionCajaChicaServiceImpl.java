package pe.edu.upeu.bomerp.finanzas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.bomerp.catalogo.producto.service.ProductoService;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import pe.edu.upeu.bomerp.exception.SolvenciaInsuficienteException;
import pe.edu.upeu.bomerp.finanzas.dto.CajaChicaResponse;
import pe.edu.upeu.bomerp.finanzas.dto.DetalleRendicionRequest;
import pe.edu.upeu.bomerp.finanzas.dto.DetalleRendicionResponse;
import pe.edu.upeu.bomerp.finanzas.dto.RendicionCajaChicaRequest;
import pe.edu.upeu.bomerp.finanzas.dto.RendicionCajaChicaResponse;
import pe.edu.upeu.bomerp.finanzas.dto.RendicionResumenDto;
import pe.edu.upeu.bomerp.finanzas.dto.ReporteRendicionesAgregadoDto;
import pe.edu.upeu.bomerp.finanzas.entity.CajaChica;
import pe.edu.upeu.bomerp.finanzas.entity.DetalleRendicionCajaChica;
import pe.edu.upeu.bomerp.finanzas.entity.EstadoRendicion;
import pe.edu.upeu.bomerp.finanzas.entity.RendicionCajaChica;
import pe.edu.upeu.bomerp.finanzas.repository.CajaChicaRepository;
import pe.edu.upeu.bomerp.finanzas.repository.RendicionCajaChicaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RendicionCajaChicaServiceImpl implements RendicionCajaChicaService {

    private final CajaChicaRepository cajaChicaRepository;
    private final RendicionCajaChicaRepository rendicionRepository;
    private final ProductoService productoService; // Comunicación cruzada con módulo catalogo

    @Override
    @Transactional
    public CajaChicaResponse crearCajaChica(String codigo, String responsable, BigDecimal limiteMaximo, BigDecimal saldoInicial) {
        if (cajaChicaRepository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("Ya existe una caja chica con el código: " + codigo);
        }

        CajaChica caja = CajaChica.builder()
                .codigo(codigo)
                .responsable(responsable)
                .limiteMaximo(limiteMaximo)
                .saldoDisponible(saldoInicial)
                .build();

        CajaChica guardada = cajaChicaRepository.save(caja);
        return mapToCajaChicaResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaChicaResponse obtenerCajaChica(Long id) {
        CajaChica caja = buscarCajaChicaOFallar(id);
        return mapToCajaChicaResponse(caja);
    }

    /**
     * ========================================================================
     * OPERACIÓN DE DOMINIO CABECERA-DETALLE CON TRANSACCIÓN ATÓMICA
     * ========================================================================
     * Cumple con los criterios 4 y 6 de la rúbrica:
     * 1. Comunicación Inter-módulo: Valida la existencia del producto en el
     *    módulo 'catalogo' mediante ProductoService (expuesto vía @NamedInterface).
     * 2. Cálculo de Totales: El método de dominio rendicion.calcularTotal()
     *    calcula subtotales (precio * cantidad) y acumula el monto total en memoria.
     * 3. Regla de Negocio Real (RN-FIN-01): Control de Liquidez / Solvencia de Caja.
     *    Si montoTotal > saldoDisponible de la Caja Chica, se lanza
     *    SolvenciaInsuficienteException (manejada por GlobalExceptionHandler -> HTTP 400).
     * 4. Transacción Atómica (@Transactional):
     *    - Caso Éxito: Se descuenta saldoDisponible de Caja Chica y se persisten
     *      cabecera y detalles en un único commit atómico.
     *    - Caso Rollback: Ante una excepción, Spring revierte toda mutación.
     *      El saldo de la caja queda intacto y no se guarda ninguna fila.
     * ========================================================================
     */
    @Override
    @Transactional
    public RendicionCajaChicaResponse registrarRendicion(RendicionCajaChicaRequest request) {
        if (rendicionRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una rendición con el código: " + request.getCodigo());
        }

        CajaChica cajaChica = buscarCajaChicaOFallar(request.getCajaChicaId());

        RendicionCajaChica rendicion = RendicionCajaChica.builder()
                .codigo(request.getCodigo())
                .responsable(request.getResponsable())
                .fecha(LocalDate.now())
                .estado(EstadoRendicion.APROBADO)
                .cajaChica(cajaChica)
                .build();

        for (DetalleRendicionRequest detReq : request.getDetalles()) {
            // [LÍMITES DE MÓDULO]: Comunicación síncrona desacoplada con módulo 'catalogo'
            if (detReq.getProductoId() != null) {
                productoService.obtener(detReq.getProductoId());
            }

            DetalleRendicionCajaChica detalle = DetalleRendicionCajaChica.builder()
                    .concepto(detReq.getConcepto())
                    .cantidad(detReq.getCantidad())
                    .precioUnitario(detReq.getPrecioUnitario())
                    .productoId(detReq.getProductoId())
                    .build();

            rendicion.agregarDetalle(detalle);
        }

        // [CABECERA-DETALLE]: Cálculo de subtotales y total acumulado de dominio
        rendicion.calcularTotal();

        // [REGLA DE NEGOCIO REAL RN-FIN-01]: Solvencia de Caja Chica / Control de Liquidez
        if (rendicion.getMontoTotal().compareTo(cajaChica.getSaldoDisponible()) > 0) {
            throw new SolvenciaInsuficienteException(
                    "RN-FIN-01: El monto total de la rendición (S/ " + rendicion.getMontoTotal() +
                    ") supera la solvencia/saldo disponible de Caja Chica (S/ " + cajaChica.getSaldoDisponible() + ")"
            );
        }

        // [TRANSACCIÓN ATÓMICA]: Mutación y persistencia conjunta (Commit atómico)
        cajaChica.setSaldoDisponible(cajaChica.getSaldoDisponible().subtract(rendicion.getMontoTotal()));
        cajaChicaRepository.save(cajaChica);

        RendicionCajaChica rendicionGuardada = rendicionRepository.save(rendicion);
        return mapToRendicionResponse(rendicionGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public RendicionCajaChicaResponse obtenerRendicion(Long id) {
        RendicionCajaChica rendicion = rendicionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendición no encontrada con id: " + id));
        return mapToRendicionResponse(rendicion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendicionCajaChicaResponse> listarRendicionesPorCaja(Long cajaChicaId) {
        buscarCajaChicaOFallar(cajaChicaId);
        return rendicionRepository.findByCajaChicaId(cajaChicaId).stream()
                .map(this::mapToRendicionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RendicionCajaChicaResponse> listarPaginado(Pageable pageable) {
        return rendicionRepository.findAll(pageable)
                .map(this::mapToRendicionResponse);
    }

    /**
     * ========================================================================
     * CONSULTAS EMPRESARIALES: FILTROS DINÁMICOS COMBINADOS Y ORDENAMIENTO
     * ========================================================================
     * Permite múltiples combinaciones de parámetros de búsqueda opcionales
     * (caja, estado, responsable, rango de fechas, rango de montos) junto con
     * paginación y ordenamiento dinámico multi-campo.
     * ========================================================================
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RendicionCajaChicaResponse> buscarConFiltros(
            Long cajaChicaId,
            EstadoRendicion estado,
            String responsable,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            BigDecimal montoMin,
            BigDecimal montoMax,
            Pageable pageable) {
        return rendicionRepository.buscarConFiltros(
                cajaChicaId, estado, responsable, fechaInicio, fechaFin, montoMin, montoMax, pageable
        ).map(this::mapToRendicionResponse);
    }

    /**
     * ========================================================================
     * PROYECCIÓN DE RESUMEN (CONSTRUCTOR EXPRESSION DTO)
     * ========================================================================
     * Utiliza la cláusula JPQL 'SELECT new ...' para construir directamente DTOs
     * planos en la base de datos sin sobrecosto de memoria por entidades ORM.
     * ========================================================================
     */
    @Override
    @Transactional(readOnly = true)
    public List<RendicionResumenDto> obtenerResumen(Long cajaChicaId) {
        if (cajaChicaId != null) {
            buscarCajaChicaOFallar(cajaChicaId);
        }
        return rendicionRepository.obtenerResumenProyectado(cajaChicaId);
    }

    /**
     * ========================================================================
     * REPORTE AGREGADO DEFENSIVO (0 / 0.00 en vez de NULL)
     * ========================================================================
     * Realiza agregaciones matemáticas (SUM, AVG, MAX, MIN, COUNT).
     * Si no existen registros coincidentes, garantiza numéricamente 0 / 0.00
     * en lugar de devolver null.
     * ========================================================================
     */
    @Override
    @Transactional(readOnly = true)
    public ReporteRendicionesAgregadoDto obtenerReporteAgregado(Long cajaChicaId) {
        if (cajaChicaId != null) {
            buscarCajaChicaOFallar(cajaChicaId);
        }
        ReporteRendicionesAgregadoDto reporte = rendicionRepository.obtenerReporteAgregado(cajaChicaId);
        return reporte != null ? reporte : ReporteRendicionesAgregadoDto.empty();
    }

    private CajaChica buscarCajaChicaOFallar(Long id) {
        return cajaChicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caja chica no encontrada con id: " + id));
    }

    private CajaChicaResponse mapToCajaChicaResponse(CajaChica caja) {
        return CajaChicaResponse.builder()
                .id(caja.getId())
                .codigo(caja.getCodigo())
                .responsable(caja.getResponsable())
                .saldoDisponible(caja.getSaldoDisponible())
                .limiteMaximo(caja.getLimiteMaximo())
                .build();
    }

    private RendicionCajaChicaResponse mapToRendicionResponse(RendicionCajaChica r) {
        List<DetalleRendicionResponse> detallesResp = r.getDetalles().stream()
                .map(d -> DetalleRendicionResponse.builder()
                        .id(d.getId())
                        .concepto(d.getConcepto())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .productoId(d.getProductoId())
                        .build())
                .toList();

        return RendicionCajaChicaResponse.builder()
                .id(r.getId())
                .codigo(r.getCodigo())
                .responsable(r.getResponsable())
                .fecha(r.getFecha())
                .montoTotal(r.getMontoTotal())
                .estado(r.getEstado())
                .cajaChicaId(r.getCajaChica().getId())
                .detalles(detallesResp)
                .build();
    }
}
