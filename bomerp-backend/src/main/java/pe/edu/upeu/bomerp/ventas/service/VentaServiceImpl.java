package pe.edu.upeu.bomerp.ventas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import pe.edu.upeu.bomerp.catalogo.producto.repository.ProductoRepository;
import pe.edu.upeu.bomerp.finanzas.dto.MovimientoCajaRequest;
import pe.edu.upeu.bomerp.finanzas.dto.SesionCajaResponse;
import pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera;
import pe.edu.upeu.bomerp.finanzas.entity.MetodoPago;
import pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero;
import pe.edu.upeu.bomerp.finanzas.repository.CategoriaFinancieraRepository;
import pe.edu.upeu.bomerp.finanzas.service.SesionCajaService;
import pe.edu.upeu.bomerp.inventario.dto.DescuentoFefoRequest;
import pe.edu.upeu.bomerp.inventario.dto.DescuentoFefoResponse;
import pe.edu.upeu.bomerp.inventario.service.InventarioService;
import pe.edu.upeu.bomerp.ventas.dto.CanalVentaResumenDto;
import pe.edu.upeu.bomerp.ventas.dto.DetalleVentaResponse;
import pe.edu.upeu.bomerp.ventas.dto.ProductoRentabilidadDto;
import pe.edu.upeu.bomerp.ventas.dto.ReporteOmnicanalResponse;
import pe.edu.upeu.bomerp.ventas.dto.VentaCreateRequest;
import pe.edu.upeu.bomerp.ventas.dto.VentaItemRequest;
import pe.edu.upeu.bomerp.ventas.dto.VentaResponse;
import pe.edu.upeu.bomerp.ventas.entity.CanalVenta;
import pe.edu.upeu.bomerp.ventas.entity.DetalleVenta;
import pe.edu.upeu.bomerp.ventas.entity.EstadoVenta;
import pe.edu.upeu.bomerp.ventas.entity.MetodoPagoVenta;
import pe.edu.upeu.bomerp.ventas.entity.Venta;
import pe.edu.upeu.bomerp.ventas.repository.VentaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final InventarioService inventarioService;
    private final SesionCajaService sesionCajaService;
    private final ProductoRepository productoRepository;
    private final CategoriaFinancieraRepository categoriaFinancieraRepository;

    @Override
    public VentaResponse registrarVenta(VentaCreateRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La venta debe contener al menos un producto");
        }

        // 1. Generar correlativo de venta único (Ej: VTA-20261019-143025)
        String timestampCorrelativo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String codigoVenta = "VTA-" + timestampCorrelativo;

        // 2. Calcular montos de la venta
        BigDecimal total = BigDecimal.ZERO;
        for (VentaItemRequest item : request.getItems()) {
            BigDecimal sub = item.getCantidad().multiply(item.getPrecioUnitario());
            total = total.add(sub);
        }
        total = total.setScale(2, RoundingMode.HALF_UP);

        BigDecimal subtotal = total.divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal);

        BigDecimal montoPagado = request.getMontoPagado() != null ? request.getMontoPagado() : total;
        BigDecimal vuelto = BigDecimal.ZERO;

        // 3. Validación y Sincronización con Turno de Caja (SesionCaja)
        SesionCajaResponse sesionActiva = null;
        try {
            sesionActiva = sesionCajaService.obtenerUltimaSesionActiva();
        } catch (Exception e) {
            log.warn("No se encontró sesión activa de caja: {}", e.getMessage());
        }

        if (request.getMetodoPago() == MetodoPagoVenta.EFECTIVO) {
            if (sesionActiva == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "RN-FIN-02: Se requiere una sesión de caja abierta para registrar ventas en efectivo en mostrador.");
            }

            if (montoPagado.compareTo(total) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("El monto pagado en efectivo (S/ %s) es menor al total a pagar (S/ %s)", montoPagado, total));
            }
            vuelto = montoPagado.subtract(total);
        }

        // 4. Instanciar Cabecera de Venta
        Venta venta = Venta.builder()
                .codigoVenta(codigoVenta)
                .fechaHora(LocalDateTime.now())
                .canal(request.getCanal())
                .clienteTipo(request.getClienteTipo() != null ? request.getClienteTipo() : "CONSUMIDOR_FINAL")
                .clienteNombre(request.getClienteNombre() != null && !request.getClienteNombre().isBlank() ? request.getClienteNombre() : "Público General")
                .clienteNumeroDoc(request.getClienteNumeroDoc())
                .metodoPago(request.getMetodoPago())
                .subtotal(subtotal)
                .igv(igv)
                .total(total)
                .montoPagado(montoPagado)
                .vuelto(vuelto)
                .sesionCajaId(sesionActiva != null ? sesionActiva.getId() : null)
                .cajeroNombre(request.getCajeroNombre() != null ? request.getCajeroNombre() : (sesionActiva != null ? sesionActiva.getCajero() : "Cajero POS"))
                .estado(EstadoVenta.EMITIDA)
                .detalles(new ArrayList<>())
                .build();

        // 5. Descuento de inventario bajo estricta regla FEFO y creación de líneas de venta
        for (VentaItemRequest item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Producto no encontrado con ID: " + item.getProductoId()));

            DescuentoFefoRequest fefoReq = DescuentoFefoRequest.builder()
                    .productoId(producto.getId())
                    .cantidad(item.getCantidad())
                    .motivo("VENTA_MOSTRADOR_POS")
                    .documentoReferencia(codigoVenta)
                    .build();

            // Ejecuta FEFO: consume primero los lotes con fecha de vencimiento más próxima
            DescuentoFefoResponse fefoRes = inventarioService.descontarStockFEFO(fefoReq);

            String lotesResumen = fefoRes.getLotesAfectados().stream()
                    .map(l -> l.getCodigoLote() + " (" + l.getCantidadDescontada() + " un.)")
                    .collect(Collectors.joining(", "));

            BigDecimal itemSubtotal = item.getCantidad().multiply(item.getPrecioUnitario()).setScale(2, RoundingMode.HALF_UP);

            DetalleVenta det = DetalleVenta.builder()
                    .productoId(producto.getId())
                    .productoNombre(producto.getNombre())
                    .productoImagenUrl(producto.getImagenUrl())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .subtotal(itemSubtotal)
                    .lotesConsumidosFefo(lotesResumen)
                    .build();

            venta.addDetalle(det);
        }

        Venta guardada = ventaRepository.save(venta);

        // 6. Impacto en Gaveta de Efectivo: Registrar INGRESO directo en Sesión de Caja Activa
        if (request.getMetodoPago() == MetodoPagoVenta.EFECTIVO && sesionActiva != null) {
            CategoriaFinanciera catVentas = categoriaFinancieraRepository.findAll().stream()
                    .filter(c -> c.getTipoDefault() == TipoFinanciero.INGRESO)
                    .findFirst()
                    .orElse(null);

            Long catId = catVentas != null ? catVentas.getId() : 1L;

            MovimientoCajaRequest movReq = MovimientoCajaRequest.builder()
                    .tipo(TipoFinanciero.INGRESO)
                    .metodoPago(MetodoPago.EFECTIVO)
                    .monto(total)
                    .descripcion("Venta mostrador ticket: " + codigoVenta)
                    .referenciaExterna(codigoVenta)
                    .categoriaFinancieraId(catId)
                    .build();

            sesionCajaService.registrarMovimiento(sesionActiva.getId(), movReq);
            log.info("Venta {} en efectivo impactó la sesión de caja ID {} con ingreso de S/ {}",
                    codigoVenta, sesionActiva.getId(), total);
        }

        log.info("Venta {} procesada con éxito por un total de S/ {}", codigoVenta, total);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponse> listarVentas() {
        return ventaRepository.findAllByOrderByFechaHoraDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse obtenerVentaPorId(Long id) {
        Venta v = ventaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con ID: " + id));
        return mapToResponse(v);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse obtenerVentaPorCodigo(String codigoVenta) {
        Venta v = ventaRepository.findByCodigoVenta(codigoVenta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con código: " + codigoVenta));
        return mapToResponse(v);
    }

    private VentaResponse mapToResponse(Venta v) {
        List<DetalleVentaResponse> dets = v.getDetalles().stream()
                .map(d -> DetalleVentaResponse.builder()
                        .id(d.getId())
                        .productoId(d.getProductoId())
                        .productoNombre(d.getProductoNombre())
                        .productoImagenUrl(d.getProductoImagenUrl())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .lotesConsumidosFefo(d.getLotesConsumidosFefo())
                        .build())
                .toList();

        return VentaResponse.builder()
                .id(v.getId())
                .codigoVenta(v.getCodigoVenta())
                .fechaHora(v.getFechaHora())
                .canal(v.getCanal())
                .clienteTipo(v.getClienteTipo())
                .clienteNombre(v.getClienteNombre())
                .clienteNumeroDoc(v.getClienteNumeroDoc())
                .metodoPago(v.getMetodoPago())
                .subtotal(v.getSubtotal())
                .igv(v.getIgv())
                .total(v.getTotal())
                .montoPagado(v.getMontoPagado())
                .vuelto(v.getVuelto())
                .sesionCajaId(v.getSesionCajaId())
                .cajeroNombre(v.getCajeroNombre())
                .estado(v.getEstado())
                .detalles(dets)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public pe.edu.upeu.bomerp.ventas.dto.ReporteOmnicanalResponse obtenerReporteOmnicanal() {
        List<Venta> todasVentas = ventaRepository.findAll();

        BigDecimal totalOmnicanal = BigDecimal.ZERO;
        for (Venta v : todasVentas) {
            if (v.getTotal() != null) {
                totalOmnicanal = totalOmnicanal.add(v.getTotal());
            }
        }

        // 1. Resumen por Canal
        java.util.Map<CanalVenta, BigDecimal> ventasPorCanalMap = new java.util.EnumMap<>(CanalVenta.class);
        java.util.Map<CanalVenta, Long> conteoPorCanalMap = new java.util.EnumMap<>(CanalVenta.class);
        for (CanalVenta c : CanalVenta.values()) {
            ventasPorCanalMap.put(c, BigDecimal.ZERO);
            conteoPorCanalMap.put(c, 0L);
        }

        for (Venta v : todasVentas) {
            CanalVenta c = v.getCanal();
            if (c != null && v.getTotal() != null) {
                ventasPorCanalMap.put(c, ventasPorCanalMap.get(c).add(v.getTotal()));
                conteoPorCanalMap.put(c, conteoPorCanalMap.get(c) + 1);
            }
        }

        List<pe.edu.upeu.bomerp.ventas.dto.CanalVentaResumenDto> canalesDto = new ArrayList<>();
        for (CanalVenta c : CanalVenta.values()) {
            BigDecimal subtotalCanal = ventasPorCanalMap.get(c);
            Long transacciones = conteoPorCanalMap.get(c);
            BigDecimal ticketPromedio = transacciones > 0
                    ? subtotalCanal.divide(BigDecimal.valueOf(transacciones), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            BigDecimal porcentaje = totalOmnicanal.compareTo(BigDecimal.ZERO) > 0
                    ? subtotalCanal.multiply(BigDecimal.valueOf(100)).divide(totalOmnicanal, 1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            String nombreAmigable;
            switch (c) {
                case TIENDA_MOSTRADOR -> nombreAmigable = "Tienda Física / Mostrador";
                case FERIA_COMERCIAL -> nombreAmigable = "Feria Agropecuaria / Eventos";
                case DELIVERY -> nombreAmigable = "Canal Delivery / Envíos";
                default -> nombreAmigable = c.name();
            }

            canalesDto.add(pe.edu.upeu.bomerp.ventas.dto.CanalVentaResumenDto.builder()
                    .canal(c)
                    .canalNombre(nombreAmigable)
                    .totalVentas(subtotalCanal)
                    .cantidadTransacciones(transacciones)
                    .ticketPromedio(ticketPromedio)
                    .porcentaje(porcentaje)
                    .build());
        }

        // 2. Ranking de Productos (Matriz ABC)
        java.util.Map<Long, pe.edu.upeu.bomerp.ventas.dto.ProductoRentabilidadDto> prodMap = new java.util.HashMap<>();
        for (Venta v : todasVentas) {
            for (DetalleVenta d : v.getDetalles()) {
                Long pid = d.getProductoId();
                pe.edu.upeu.bomerp.ventas.dto.ProductoRentabilidadDto dto = prodMap.get(pid);
                if (dto == null) {
                    dto = pe.edu.upeu.bomerp.ventas.dto.ProductoRentabilidadDto.builder()
                            .productoId(pid)
                            .productoNombre(d.getProductoNombre())
                            .categoria("Agroindustria")
                            .unidadesVendidas(BigDecimal.ZERO)
                            .totalIngresos(BigDecimal.ZERO)
                            .costoEstimado(BigDecimal.ZERO)
                            .utilidadBruta(BigDecimal.ZERO)
                            .margenContribucion(BigDecimal.ZERO)
                            .clasificacionAbc("A")
                            .build();
                    prodMap.put(pid, dto);
                }
                dto.setUnidadesVendidas(dto.getUnidadesVendidas().add(d.getCantidad()));
                dto.setTotalIngresos(dto.getTotalIngresos().add(d.getSubtotal()));
            }
        }

        List<pe.edu.upeu.bomerp.ventas.dto.ProductoRentabilidadDto> ranking = new ArrayList<>(prodMap.values());
        // Ordenar por totalIngresos descendente
        ranking.sort((a, b) -> b.getTotalIngresos().compareTo(a.getTotalIngresos()));

        BigDecimal acumulado = BigDecimal.ZERO;
        for (pe.edu.upeu.bomerp.ventas.dto.ProductoRentabilidadDto p : ranking) {
            // Costo estimado referencial 55% para cálculo de margen en agroindustria
            BigDecimal costo = p.getTotalIngresos().multiply(new BigDecimal("0.55")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal utilidad = p.getTotalIngresos().subtract(costo);
            BigDecimal margen = p.getTotalIngresos().compareTo(BigDecimal.ZERO) > 0
                    ? utilidad.multiply(BigDecimal.valueOf(100)).divide(p.getTotalIngresos(), 1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            p.setCostoEstimado(costo);
            p.setUtilidadBruta(utilidad);
            p.setMargenContribucion(margen);

            acumulado = acumulado.add(p.getTotalIngresos());
            if (totalOmnicanal.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal pctAcum = acumulado.multiply(BigDecimal.valueOf(100)).divide(totalOmnicanal, 1, RoundingMode.HALF_UP);
                if (pctAcum.compareTo(new BigDecimal("80")) <= 0) {
                    p.setClasificacionAbc("A");
                } else if (pctAcum.compareTo(new BigDecimal("95")) <= 0) {
                    p.setClasificacionAbc("B");
                } else {
                    p.setClasificacionAbc("C");
                }
            } else {
                p.setClasificacionAbc("A");
            }
        }

        return pe.edu.upeu.bomerp.ventas.dto.ReporteOmnicanalResponse.builder()
                .totalVentasOmnicanal(totalOmnicanal)
                .totalTransacciones((long) todasVentas.size())
                .canales(canalesDto)
                .rankingProductos(ranking)
                .build();
    }
}
