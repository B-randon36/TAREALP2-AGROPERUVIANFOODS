package pe.edu.upeu.bomerp.produccion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.bomerp.inventario.dto.LoteCreateDto;
import pe.edu.upeu.bomerp.inventario.dto.LoteDto;
import pe.edu.upeu.bomerp.inventario.service.InventarioService;
import pe.edu.upeu.bomerp.produccion.dto.CompletarOrdenRequest;
import pe.edu.upeu.bomerp.produccion.dto.CrearOrdenRequest;
import pe.edu.upeu.bomerp.produccion.dto.DetalleConsumoDto;
import pe.edu.upeu.bomerp.produccion.dto.OrdenProduccionDto;
import pe.edu.upeu.bomerp.produccion.entity.DetalleConsumoInsumo;
import pe.edu.upeu.bomerp.produccion.entity.DetalleRecetaInsumo;
import pe.edu.upeu.bomerp.produccion.entity.EstadoOrdenProduccion;
import pe.edu.upeu.bomerp.produccion.entity.Insumo;
import pe.edu.upeu.bomerp.produccion.entity.OrdenProduccion;
import pe.edu.upeu.bomerp.produccion.entity.RecetaBOM;
import pe.edu.upeu.bomerp.produccion.repository.InsumoRepository;
import pe.edu.upeu.bomerp.produccion.repository.OrdenProduccionRepository;
import pe.edu.upeu.bomerp.produccion.repository.RecetaBomRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrdenProduccionServiceImpl implements OrdenProduccionService {

    private final OrdenProduccionRepository ordenProduccionRepository;
    private final RecetaBomRepository recetaBomRepository;
    private final InsumoRepository insumoRepository;
    private final InventarioService inventarioService;

    @Override
    @Transactional(readOnly = true)
    public List<OrdenProduccionDto> listarTodas() {
        return ordenProduccionRepository.findAllByOrderByFechaRegistroDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenProduccionDto obtenerPorId(Long id) {
        return toDto(ordenProduccionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de producción no encontrada: " + id)));
    }

    @Override
    public OrdenProduccionDto crearOrden(CrearOrdenRequest req) {
        if (ordenProduccionRepository.findByCodigoOrden(req.getCodigoOrden()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una orden con el código: " + req.getCodigoOrden());
        }

        RecetaBOM receta = recetaBomRepository.findById(req.getRecetaBomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receta BOM no encontrada: " + req.getRecetaBomId()));

        if (!Boolean.TRUE.equals(receta.getVigente())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La receta seleccionada no se encuentra vigente");
        }

        // Validación de stock teórico de insumos para la cantidad a producir
        for (DetalleRecetaInsumo itemReceta : receta.getDetalles()) {
            Insumo insumo = itemReceta.getInsumo();
            BigDecimal requerido = itemReceta.getCantidadRequerida().multiply(req.getCantidadAProducir());
            if (insumo.getStockActual().compareTo(requerido) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Stock insuficiente de '%s'. Requerido: %s %s, Disponible: %s %s",
                                insumo.getNombre(), requerido, insumo.getUnidadMedida(),
                                insumo.getStockActual(), insumo.getUnidadMedida()));
            }
        }

        OrdenProduccion orden = OrdenProduccion.builder()
                .codigoOrden(req.getCodigoOrden())
                .productoId(receta.getProductoId())
                .productoNombre(receta.getProductoNombre())
                .recetaBomId(receta.getId())
                .cantidadAProducir(req.getCantidadAProducir())
                .fechaRegistro(LocalDateTime.now())
                .estado(EstadoOrdenProduccion.PLANIFICADA)
                .observaciones(req.getObservaciones())
                .build();

        return toDto(ordenProduccionRepository.save(orden));
    }

    @Override
    public OrdenProduccionDto completarOrden(Long id, CompletarOrdenRequest req) {
        OrdenProduccion orden = ordenProduccionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de producción no encontrada: " + id));

        if (orden.getEstado() == EstadoOrdenProduccion.COMPLETADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La orden ya se encuentra COMPLETADA");
        }
        if (orden.getEstado() == EstadoOrdenProduccion.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede completar una orden CANCELADA");
        }

        RecetaBOM receta = recetaBomRepository.findById(orden.getRecetaBomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receta no encontrada"));

        // 1. Descontar insumos consumidos de almacén y registrar consumo
        for (DetalleRecetaInsumo itemReceta : receta.getDetalles()) {
            Insumo insumo = insumoRepository.findById(itemReceta.getInsumo().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo no encontrado"));

            BigDecimal consumoTotal = itemReceta.getCantidadRequerida().multiply(orden.getCantidadAProducir());
            if (insumo.getStockActual().compareTo(consumoTotal) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para completar orden. Insumo: " + insumo.getNombre());
            }

            insumo.setStockActual(insumo.getStockActual().subtract(consumoTotal));
            insumoRepository.save(insumo);

            DetalleConsumoInsumo consumo = DetalleConsumoInsumo.builder()
                    .insumo(insumo)
                    .cantidadConsumida(consumoTotal)
                    .costoUnitario(insumo.getCostoUnitario())
                    .build();

            orden.addConsumo(consumo);
        }

        // 2. Determinar fechas de fabricación y caducidad para el nuevo lote en inventario
        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento;
        if (req != null && req.getFechaVencimientoPersonalizada() != null) {
            vencimiento = req.getFechaVencimientoPersonalizada();
        } else {
            // Regla de caducidad de Agro Peruvian Foods: Cacao 18 meses, Otros 12 meses
            if (receta.getProductoNombre() != null && receta.getProductoNombre().toLowerCase().contains("cacao")) {
                vencimiento = hoy.plusMonths(18);
            } else {
                vencimiento = hoy.plusMonths(12);
            }
        }

        String codigoLote = String.format("LOT-PROD-%s-%s-%03d",
                orden.getProductoId(),
                hoy.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                orden.getId() % 1000);

        // 3. Crear e ingresar automáticamente el Lote a través del InventarioService
        BigDecimal costoUnitarioProduccion = receta.getCostoTotalProduccion() != null && receta.getCostoTotalProduccion().compareTo(BigDecimal.ZERO) > 0
                ? receta.getCostoTotalProduccion()
                : new BigDecimal("15.00");

        LoteCreateDto loteRequest = LoteCreateDto.builder()
                .codigoLote(codigoLote)
                .productoId(orden.getProductoId())
                .fechaFabricacion(hoy)
                .fechaVencimiento(vencimiento)
                .stockInicial(orden.getCantidadAProducir())
                .costoUnitario(costoUnitarioProduccion)
                .observaciones("Ingreso automático por fabricación terminada en OP: " + orden.getCodigoOrden())
                .build();

        LoteDto nuevoLote = inventarioService.registrarLote(loteRequest);

        // 4. Actualizar estado de la orden
        orden.setLoteGeneradoId(nuevoLote.getId());
        orden.setCodigoLoteGenerado(nuevoLote.getCodigoLote());
        orden.setEstado(EstadoOrdenProduccion.COMPLETADA);
        orden.setFechaFinalizacion(LocalDateTime.now());
        if (req != null && req.getObservaciones() != null && !req.getObservaciones().isBlank()) {
            orden.setObservaciones((orden.getObservaciones() != null ? orden.getObservaciones() + " | " : "") + req.getObservaciones());
        }

        log.info("Orden de producción {} completada exitosamente. Lote generado: {}", orden.getCodigoOrden(), nuevoLote.getCodigoLote());
        return toDto(ordenProduccionRepository.save(orden));
    }

    @Override
    public OrdenProduccionDto cancelarOrden(Long id, String motivo) {
        OrdenProduccion orden = ordenProduccionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden de producción no encontrada: " + id));

        if (orden.getEstado() == EstadoOrdenProduccion.COMPLETADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede cancelar una orden ya COMPLETADA");
        }

        orden.setEstado(EstadoOrdenProduccion.CANCELADA);
        orden.setObservaciones((orden.getObservaciones() != null ? orden.getObservaciones() + " | Cancelada: " : "Cancelada: ") + motivo);
        return toDto(ordenProduccionRepository.save(orden));
    }

    private OrdenProduccionDto toDto(OrdenProduccion o) {
        List<DetalleConsumoDto> consumos = o.getConsumos() == null ? List.of() :
                o.getConsumos().stream()
                        .map(c -> DetalleConsumoDto.builder()
                                .id(c.getId())
                                .insumoId(c.getInsumo().getId())
                                .insumoCodigo(c.getInsumo().getCodigo())
                                .insumoNombre(c.getInsumo().getNombre())
                                .unidadMedida(c.getInsumo().getUnidadMedida())
                                .cantidadConsumida(c.getCantidadConsumida())
                                .costoUnitario(c.getCostoUnitario())
                                .build())
                        .toList();

        return OrdenProduccionDto.builder()
                .id(o.getId())
                .codigoOrden(o.getCodigoOrden())
                .productoId(o.getProductoId())
                .productoNombre(o.getProductoNombre())
                .recetaBomId(o.getRecetaBomId())
                .cantidadAProducir(o.getCantidadAProducir())
                .loteGeneradoId(o.getLoteGeneradoId())
                .codigoLoteGenerado(o.getCodigoLoteGenerado())
                .fechaRegistro(o.getFechaRegistro())
                .fechaFinalizacion(o.getFechaFinalizacion())
                .estado(o.getEstado())
                .observaciones(o.getObservaciones())
                .consumos(consumos)
                .build();
    }
}
