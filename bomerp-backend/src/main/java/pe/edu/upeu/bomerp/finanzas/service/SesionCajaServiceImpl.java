package pe.edu.upeu.bomerp.finanzas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import pe.edu.upeu.bomerp.exception.SolvenciaInsuficienteException;
import pe.edu.upeu.bomerp.finanzas.dto.AperturaSesionRequest;
import pe.edu.upeu.bomerp.finanzas.dto.CierreCajaReporteDto;
import pe.edu.upeu.bomerp.finanzas.dto.CierreSesionRequest;
import pe.edu.upeu.bomerp.finanzas.dto.MetodoPagoResumenDto;
import pe.edu.upeu.bomerp.finanzas.dto.MovimientoCajaRequest;
import pe.edu.upeu.bomerp.finanzas.dto.MovimientoCajaResponse;
import pe.edu.upeu.bomerp.finanzas.dto.SesionCajaDetalleResponse;
import pe.edu.upeu.bomerp.finanzas.dto.SesionCajaResponse;
import pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera;
import pe.edu.upeu.bomerp.finanzas.entity.EstadoCaja;
import pe.edu.upeu.bomerp.finanzas.entity.MetodoPago;
import pe.edu.upeu.bomerp.finanzas.entity.MovimientoCaja;
import pe.edu.upeu.bomerp.finanzas.entity.SesionCaja;
import pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero;
import pe.edu.upeu.bomerp.finanzas.repository.CategoriaFinancieraRepository;
import pe.edu.upeu.bomerp.finanzas.repository.MovimientoCajaRepository;
import pe.edu.upeu.bomerp.finanzas.repository.SesionCajaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class SesionCajaServiceImpl implements SesionCajaService {

    private final SesionCajaRepository sesionRepository;
    private final MovimientoCajaRepository movimientoRepository;
    private final CategoriaFinancieraRepository categoriaRepository;

    public SesionCajaServiceImpl(
            SesionCajaRepository sesionRepository,
            MovimientoCajaRepository movimientoRepository,
            CategoriaFinancieraRepository categoriaRepository) {
        this.sesionRepository = sesionRepository;
        this.movimientoRepository = movimientoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public SesionCajaResponse abrirSesion(AperturaSesionRequest request) {
        if (sesionRepository.existsByUsuarioIdAndEstado(request.getUsuarioId(), EstadoCaja.ABIERTA)) {
            throw new IllegalStateException("RN-FIN-02: El usuario ya posee un turno de caja abierto. Debe cerrarlo antes de iniciar otro.");
        }

        String codigo = request.getCodigoSesion();
        if (codigo == null || codigo.isBlank()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            codigo = "SES-" + timestamp;
        }

        if (sesionRepository.existsByCodigoSesion(codigo)) {
            throw new IllegalArgumentException("Ya existe una sesión con el código: " + codigo);
        }

        SesionCaja sesion = SesionCaja.builder()
                .codigoSesion(codigo)
                .usuarioId(request.getUsuarioId())
                .cajero(request.getCajero())
                .fechaApertura(LocalDateTime.now())
                .saldoInicial(request.getSaldoInicial())
                .totalIngresosEfectivo(BigDecimal.ZERO)
                .totalEgresosEfectivo(BigDecimal.ZERO)
                .saldoEsperadoEfectivo(request.getSaldoInicial())
                .saldoRealEfectivo(null)
                .diferencia(BigDecimal.ZERO)
                .estado(EstadoCaja.ABIERTA)
                .build();

        SesionCaja guardada = sesionRepository.save(sesion);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional
    public SesionCajaResponse cerrarSesion(Long id, CierreSesionRequest request) {
        SesionCaja sesion = buscarSesionOFallar(id);
        sesion.cerrarSesion(request.getSaldoRealEfectivo(), request.getObservaciones());
        SesionCaja guardada = sesionRepository.save(sesion);
        return mapToResponse(guardada);
    }

    /**
     * ========================================================================
     * REGISTRO TRANSACCIONAL DE MOVIMIENTO CON REGLA DE NEGOCIO RN-FIN-01
     * ========================================================================
     * 1. Valida que la sesión esté ABIERTA (RN-FIN-02).
     * 2. RN-FIN-01: Control de Liquidez / Solvencia.
     *    Si el movimiento es un EGRESO en EFECTIVO y supera el saldoEsperadoEfectivo,
     *    se lanza SolvenciaInsuficienteException, provocando un rollback @Transactional inmediato.
     * 3. Se actualizan atómicamente los totales de la sesión de caja en gaveta.
     * ========================================================================
     */
    @Override
    @Transactional
    public MovimientoCajaResponse registrarMovimiento(Long sesionId, MovimientoCajaRequest request) {
        SesionCaja sesion = buscarSesionOFallar(sesionId);

        if (sesion.getEstado() != EstadoCaja.ABIERTA) {
            throw new IllegalStateException("RN-FIN-02: La sesión " + sesion.getCodigoSesion() + " no está ABIERTA. No se pueden registrar movimientos.");
        }

        // Validación de la Categoría Financiera
        CategoriaFinanciera categoria = categoriaRepository.findById(request.getCategoriaFinancieraId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría financiera no encontrada con id: " + request.getCategoriaFinancieraId()));

        // ====================================================================
        // REGLA CRÍTICA RN-FIN-01: Control de Liquidez en Gaveta de Efectivo
        // ====================================================================
        if (request.getTipo() == TipoFinanciero.EGRESO && request.getMetodoPago() == MetodoPago.EFECTIVO) {
            BigDecimal saldoDisponible = sesion.getSaldoEsperadoEfectivo() != null ? sesion.getSaldoEsperadoEfectivo() : BigDecimal.ZERO;
            if (request.getMonto().compareTo(saldoDisponible) > 0) {
                throw new SolvenciaInsuficienteException(
                        "RN-FIN-01: El monto del egreso en efectivo (S/ " + request.getMonto() +
                        ") excede el saldo en efectivo disponible en gaveta (S/ " + saldoDisponible + ")"
                );
            }
        }

        MovimientoCaja movimiento = MovimientoCaja.builder()
                .fechaHora(LocalDateTime.now())
                .tipo(request.getTipo())
                .metodoPago(request.getMetodoPago())
                .monto(request.getMonto())
                .descripcion(request.getDescripcion())
                .referenciaExterna(request.getReferenciaExterna())
                .categoriaFinanciera(categoria)
                .sesionCaja(sesion)
                .build();

        sesion.agregarMovimiento(movimiento);
        sesionRepository.save(sesion);

        MovimientoCaja movGuardado = movimientoRepository.save(movimiento);
        return mapToMovimientoResponse(movGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SesionCajaResponse> listarSesiones() {
        return sesionRepository.findAllByOrderByFechaAperturaDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SesionCajaDetalleResponse obtenerDetalle(Long id) {
        SesionCaja sesion = buscarSesionOFallar(id);
        List<MovimientoCajaResponse> movs = sesion.getMovimientos().stream()
                .map(this::mapToMovimientoResponse)
                .toList();

        return SesionCajaDetalleResponse.builder()
                .id(sesion.getId())
                .codigoSesion(sesion.getCodigoSesion())
                .usuarioId(sesion.getUsuarioId())
                .cajero(sesion.getCajero())
                .fechaApertura(sesion.getFechaApertura())
                .fechaCierre(sesion.getFechaCierre())
                .saldoInicial(sesion.getSaldoInicial())
                .totalIngresosEfectivo(sesion.getTotalIngresosEfectivo())
                .totalEgresosEfectivo(sesion.getTotalEgresosEfectivo())
                .saldoEsperadoEfectivo(sesion.getSaldoEsperadoEfectivo())
                .saldoRealEfectivo(sesion.getSaldoRealEfectivo())
                .diferencia(sesion.getDiferencia())
                .estado(sesion.getEstado())
                .observaciones(sesion.getObservaciones())
                .movimientos(movs)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SesionCajaResponse obtenerSesionActivaUsuario(Long usuarioId) {
        SesionCaja sesion = sesionRepository.findFirstByUsuarioIdAndEstadoOrderByFechaAperturaDesc(usuarioId, EstadoCaja.ABIERTA)
                .orElseThrow(() -> new ResourceNotFoundException("No existe sesión abierta para el usuario id: " + usuarioId));
        return mapToResponse(sesion);
    }

    @Override
    @Transactional(readOnly = true)
    public SesionCajaResponse obtenerUltimaSesionActiva() {
        SesionCaja sesion = sesionRepository.findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja.ABIERTA)
                .orElseThrow(() -> new ResourceNotFoundException("No existe ninguna sesión de caja abierta actualmente."));
        return mapToResponse(sesion);
    }

    @Override
    @Transactional(readOnly = true)
    public CierreCajaReporteDto generarDatosReporte(Long id) {
        SesionCaja sesion = buscarSesionOFallar(id);
        List<MovimientoCajaResponse> movs = sesion.getMovimientos().stream()
                .map(this::mapToMovimientoResponse)
                .toList();

        Map<MetodoPago, BigDecimal[]> acumuladores = new EnumMap<>(MetodoPago.class);
        for (MetodoPago m : MetodoPago.values()) {
            acumuladores.put(m, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
        }

        for (MovimientoCaja mov : sesion.getMovimientos()) {
            MetodoPago mp = mov.getMetodoPago();
            BigDecimal[] par = acumuladores.get(mp);
            if (par != null && mov.getMonto() != null) {
                if (mov.getTipo() == TipoFinanciero.INGRESO) {
                    par[0] = par[0].add(mov.getMonto());
                } else if (mov.getTipo() == TipoFinanciero.EGRESO) {
                    par[1] = par[1].add(mov.getMonto());
                }
            }
        }

        List<MetodoPagoResumenDto> resumenMetodos = new ArrayList<>();
        for (Map.Entry<MetodoPago, BigDecimal[]> entry : acumuladores.entrySet()) {
            BigDecimal ing = entry.getValue()[0];
            BigDecimal egr = entry.getValue()[1];
            resumenMetodos.add(MetodoPagoResumenDto.builder()
                    .metodoPago(entry.getKey())
                    .totalIngresos(ing)
                    .totalEgresos(egr)
                    .neto(ing.subtract(egr))
                    .build());
        }

        return CierreCajaReporteDto.builder()
                .fechaGeneracion(LocalDateTime.now())
                .sesion(mapToResponse(sesion))
                .movimientos(movs)
                .resumenPorMetodo(resumenMetodos)
                .build();
    }

    private SesionCaja buscarSesionOFallar(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión de caja no encontrada con id: " + id));
    }

    private SesionCajaResponse mapToResponse(SesionCaja s) {
        int count = s.getMovimientos() != null ? s.getMovimientos().size() : 0;
        return SesionCajaResponse.builder()
                .id(s.getId())
                .codigoSesion(s.getCodigoSesion())
                .usuarioId(s.getUsuarioId())
                .cajero(s.getCajero())
                .fechaApertura(s.getFechaApertura())
                .fechaCierre(s.getFechaCierre())
                .saldoInicial(s.getSaldoInicial())
                .totalIngresosEfectivo(s.getTotalIngresosEfectivo())
                .totalEgresosEfectivo(s.getTotalEgresosEfectivo())
                .saldoEsperadoEfectivo(s.getSaldoEsperadoEfectivo())
                .saldoRealEfectivo(s.getSaldoRealEfectivo())
                .diferencia(s.getDiferencia())
                .estado(s.getEstado())
                .observaciones(s.getObservaciones())
                .cantidadMovimientos(count)
                .build();
    }

    private MovimientoCajaResponse mapToMovimientoResponse(MovimientoCaja m) {
        Long catId = m.getCategoriaFinanciera() != null ? m.getCategoriaFinanciera().getId() : null;
        String catNombre = m.getCategoriaFinanciera() != null ? m.getCategoriaFinanciera().getNombre() : null;
        Long sesionId = m.getSesionCaja() != null ? m.getSesionCaja().getId() : null;

        return MovimientoCajaResponse.builder()
                .id(m.getId())
                .fechaHora(m.getFechaHora())
                .tipo(m.getTipo())
                .metodoPago(m.getMetodoPago())
                .monto(m.getMonto())
                .descripcion(m.getDescripcion())
                .referenciaExterna(m.getReferenciaExterna())
                .categoriaFinancieraId(catId)
                .categoriaFinancieraNombre(catNombre)
                .sesionCajaId(sesionId)
                .build();
    }
}
