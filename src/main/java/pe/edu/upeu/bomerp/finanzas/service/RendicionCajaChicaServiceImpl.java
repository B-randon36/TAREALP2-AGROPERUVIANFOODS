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
            // Comunicación inter-módulo: Si se especifica un producto, validar su existencia en catalogo
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

        // 1. Evidencia técnica de cálculo de totales en cabecera-detalle
        rendicion.calcularTotal();

        // 2. Regla de Negocio Real: RN-FIN-01 (Solvencia de Caja Chica / Control de Liquidez)
        if (rendicion.getMontoTotal().compareTo(cajaChica.getSaldoDisponible()) > 0) {
            throw new SolvenciaInsuficienteException(
                    "RN-FIN-01: El monto total de la rendición (S/ " + rendicion.getMontoTotal() +
                    ") supera la solvencia/saldo disponible de Caja Chica (S/ " + cajaChica.getSaldoDisponible() + ")"
            );
        }

        // 3. Transacción atómica: Actualizar saldo y guardar cabecera + detalles
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
