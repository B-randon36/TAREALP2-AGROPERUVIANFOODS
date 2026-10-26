package pe.edu.upeu.bomerp.compras.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.bomerp.compras.dto.CompraDto;
import pe.edu.upeu.bomerp.compras.dto.DetalleCompraDto;
import pe.edu.upeu.bomerp.compras.dto.ItemCompraRequest;
import pe.edu.upeu.bomerp.compras.dto.RegistrarCompraRequest;
import pe.edu.upeu.bomerp.compras.entity.Compra;
import pe.edu.upeu.bomerp.compras.entity.DetalleCompra;
import pe.edu.upeu.bomerp.compras.entity.EstadoCompra;
import pe.edu.upeu.bomerp.compras.entity.Tercero;
import pe.edu.upeu.bomerp.compras.repository.CompraRepository;
import pe.edu.upeu.bomerp.compras.repository.TerceroRepository;
import pe.edu.upeu.bomerp.finanzas.dto.MovimientoCajaRequest;
import pe.edu.upeu.bomerp.finanzas.dto.SesionCajaResponse;
import pe.edu.upeu.bomerp.finanzas.entity.MetodoPago;
import pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero;
import pe.edu.upeu.bomerp.finanzas.service.SesionCajaService;
import pe.edu.upeu.bomerp.produccion.entity.Insumo;
import pe.edu.upeu.bomerp.produccion.repository.InsumoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final TerceroRepository terceroRepository;
    private final InsumoRepository insumoRepository;
    private final SesionCajaService sesionCajaService;

    @Override
    @Transactional(readOnly = true)
    public List<CompraDto> listarTodas() {
        return compraRepository.findAllByOrderByFechaCompraDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompraDto obtenerPorId(Long id) {
        return toDto(compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada: " + id)));
    }

    @Override
    public CompraDto registrarCompra(RegistrarCompraRequest req) {
        Tercero proveedor = terceroRepository.findById(req.getProveedorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado: " + req.getProveedorId()));

        String codigo = req.getCodigoCompra();
        if (codigo == null || codigo.isBlank()) {
            codigo = "COM-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-" +
                    String.format("%04d", (compraRepository.count() + 1));
        }

        if (compraRepository.findByCodigoCompra(codigo).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una compra con código: " + codigo);
        }

        BigDecimal subtotalAcumulado = BigDecimal.ZERO;

        Compra compra = Compra.builder()
                .codigoCompra(codigo)
                .proveedorId(proveedor.getId())
                .proveedorNombre(proveedor.getRazonSocial())
                .numeroComprobante(req.getNumeroComprobante())
                .fechaCompra(req.getFechaCompra())
                .subtotal(BigDecimal.ZERO)
                .igv(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .estado(EstadoCompra.RECIBIDA) // Al registrarse se da por recibida e ingresa al almacén
                .metodoPago(req.getMetodoPago().toUpperCase())
                .observaciones(req.getObservaciones())
                .build();

        for (ItemCompraRequest item : req.getItems()) {
            Insumo insumo = insumoRepository.findById(item.getInsumoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo no encontrado: " + item.getInsumoId()));

            BigDecimal subtotalItem = item.getCantidad().multiply(item.getPrecioUnitario()).setScale(2, RoundingMode.HALF_UP);
            subtotalAcumulado = subtotalAcumulado.add(subtotalItem);

            DetalleCompra detalle = DetalleCompra.builder()
                    .insumoId(insumo.getId())
                    .insumoNombre(insumo.getNombre())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .subtotal(subtotalItem)
                    .build();

            compra.addDetalle(detalle);

            // Actualización de stock y costo unitario ponderado
            BigDecimal stockActual = insumo.getStockActual() != null ? insumo.getStockActual() : BigDecimal.ZERO;
            BigDecimal costoActual = insumo.getCostoUnitario() != null ? insumo.getCostoUnitario() : BigDecimal.ZERO;

            BigDecimal inversionPrevia = stockActual.multiply(costoActual);
            BigDecimal inversionNueva = item.getCantidad().multiply(item.getPrecioUnitario());
            BigDecimal nuevoStockTotal = stockActual.add(item.getCantidad());

            BigDecimal nuevoCostoPonderado = nuevoStockTotal.compareTo(BigDecimal.ZERO) > 0 ?
                    inversionPrevia.add(inversionNueva).divide(nuevoStockTotal, 4, RoundingMode.HALF_UP) : item.getPrecioUnitario();

            insumo.setStockActual(nuevoStockTotal);
            insumo.setCostoUnitario(nuevoCostoPonderado);
            insumoRepository.save(insumo);
        }

        BigDecimal igv = subtotalAcumulado.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotalAcumulado.add(igv).setScale(2, RoundingMode.HALF_UP);

        compra.setSubtotal(subtotalAcumulado);
        compra.setIgv(igv);
        compra.setTotal(total);

        Compra guardada = compraRepository.save(compra);

        // Si el pago fue al contado / EFECTIVO, registrar egreso en la sesión de caja activa de Finanzas si está abierta
        if ("EFECTIVO".equalsIgnoreCase(req.getMetodoPago())) {
            sesionCajaService.buscarUltimaSesionActiva().ifPresent(sesion -> {
                try {
                    sesionCajaService.registrarMovimiento(sesion.getId(), MovimientoCajaRequest.builder()
                            .tipo(TipoFinanciero.EGRESO)
                            .descripcion("Compra de Insumos " + guardada.getCodigoCompra() + " - " + proveedor.getRazonSocial())
                            .monto(total)
                            .metodoPago(MetodoPago.EFECTIVO)
                            .referenciaExterna(guardada.getNumeroComprobante())
                            .categoriaFinancieraId(1L)
                            .build());
                    log.info("Egreso registrado en caja activa para la compra {}", guardada.getCodigoCompra());
                } catch (Exception e) {
                    log.warn("No se pudo registrar egreso automático en caja para la compra {}: {}", guardada.getCodigoCompra(), e.getMessage());
                }
            });
        }

        return toDto(guardada);
    }

    @Override
    public CompraDto recibirCompra(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada: " + id));

        if (compra.getEstado() == EstadoCompra.RECIBIDA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La compra ya fue RECIBIDA previamente");
        }
        if (compra.getEstado() == EstadoCompra.ANULADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede recibir una compra ANULADA");
        }

        // Incrementar stock al recibir
        for (DetalleCompra detalle : compra.getDetalles()) {
            Insumo insumo = insumoRepository.findById(detalle.getInsumoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo no encontrado: " + detalle.getInsumoId()));

            insumo.setStockActual(insumo.getStockActual().add(detalle.getCantidad()));
            insumoRepository.save(insumo);
        }

        compra.setEstado(EstadoCompra.RECIBIDA);
        return toDto(compraRepository.save(compra));
    }

    @Override
    public CompraDto anularCompra(Long id, String motivo) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada: " + id));

        if (compra.getEstado() == EstadoCompra.ANULADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La compra ya está ANULADA");
        }

        // Si ya estaba recibida, revertir el stock de insumos
        if (compra.getEstado() == EstadoCompra.RECIBIDA) {
            for (DetalleCompra detalle : compra.getDetalles()) {
                Insumo insumo = insumoRepository.findById(detalle.getInsumoId()).orElse(null);
                if (insumo != null) {
                    BigDecimal stockRestante = insumo.getStockActual().subtract(detalle.getCantidad());
                    insumo.setStockActual(stockRestante.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : stockRestante);
                    insumoRepository.save(insumo);
                }
            }
        }

        compra.setEstado(EstadoCompra.ANULADA);
        compra.setObservaciones((compra.getObservaciones() != null ? compra.getObservaciones() + " | Anulada: " : "Anulada: ") + motivo);
        return toDto(compraRepository.save(compra));
    }

    private CompraDto toDto(Compra c) {
        List<DetalleCompraDto> detalles = c.getDetalles() == null ? List.of() :
                c.getDetalles().stream()
                        .map(d -> DetalleCompraDto.builder()
                                .id(d.getId())
                                .insumoId(d.getInsumoId())
                                .insumoNombre(d.getInsumoNombre())
                                .cantidad(d.getCantidad())
                                .precioUnitario(d.getPrecioUnitario())
                                .subtotal(d.getSubtotal())
                                .build())
                        .toList();

        return CompraDto.builder()
                .id(c.getId())
                .codigoCompra(c.getCodigoCompra())
                .proveedorId(c.getProveedorId())
                .proveedorNombre(c.getProveedorNombre())
                .numeroComprobante(c.getNumeroComprobante())
                .fechaCompra(c.getFechaCompra())
                .subtotal(c.getSubtotal())
                .igv(c.getIgv())
                .total(c.getTotal())
                .estado(c.getEstado())
                .metodoPago(c.getMetodoPago())
                .observaciones(c.getObservaciones())
                .detalles(detalles)
                .build();
    }
}
