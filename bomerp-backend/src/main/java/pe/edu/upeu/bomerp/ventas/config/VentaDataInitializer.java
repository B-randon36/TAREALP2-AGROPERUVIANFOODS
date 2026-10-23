package pe.edu.upeu.bomerp.ventas.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import pe.edu.upeu.bomerp.catalogo.producto.repository.ProductoRepository;
import pe.edu.upeu.bomerp.ventas.entity.CanalVenta;
import pe.edu.upeu.bomerp.ventas.entity.DetalleVenta;
import pe.edu.upeu.bomerp.ventas.entity.EstadoVenta;
import pe.edu.upeu.bomerp.ventas.entity.MetodoPagoVenta;
import pe.edu.upeu.bomerp.ventas.entity.Venta;
import pe.edu.upeu.bomerp.ventas.repository.VentaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Configuration
@Order(30)
@ConditionalOnProperty(name = "app.init.demo-data", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class VentaDataInitializer implements CommandLineRunner {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) {
        if (ventaRepository.count() == 0) {
            log.info("Inicializando ventas demo omnicanal (Tienda, Feria, Delivery)...");
            List<Producto> productos = productoRepository.findAll();
            if (productos.isEmpty()) {
                return;
            }

            Producto coco500 = productos.stream().filter(p -> p.getNombre().contains("500ml")).findFirst().orElse(productos.get(0));
            Producto cacao = productos.stream().filter(p -> p.getNombre().contains("Cacao")).findFirst().orElse(productos.get(0));
            Producto nibs = productos.stream().filter(p -> p.getNombre().contains("Nibs")).findFirst().orElse(productos.get(0));

            // Venta 1: Tienda Física / Mostrador (Aceite Coco 500ml + Pasta Cacao)
            crearVenta(
                    "VTA-DEMO-001",
                    CanalVenta.TIENDA_MOSTRADOR,
                    MetodoPagoVenta.EFECTIVO,
                    "CLIENTE_FINAL",
                    "Cliente Mostrador",
                    LocalDateTime.now().minusHours(4),
                    List.of(
                            crearDetalle(coco500, new BigDecimal("2.00"), new BigDecimal("48.00")),
                            crearDetalle(cacao, new BigDecimal("1.00"), new BigDecimal("32.00"))
                    )
            );

            // Venta 2: Feria Agropecuaria (Nibs de Cacao + Coco)
            crearVenta(
                    "VTA-DEMO-002",
                    CanalVenta.FERIA_COMERCIAL,
                    MetodoPagoVenta.TARJETA,
                    "CLIENTE_FINAL",
                    "Visitante Feria Tarapoto",
                    LocalDateTime.now().minusHours(3),
                    List.of(
                            crearDetalle(nibs, new BigDecimal("4.00"), new BigDecimal("18.50")),
                            crearDetalle(coco500, new BigDecimal("1.00"), new BigDecimal("48.00"))
                    )
            );

            // Venta 3: Canal Delivery (Pedido web / WhatsApp)
            crearVenta(
                    "VTA-DEMO-003",
                    CanalVenta.DELIVERY,
                    MetodoPagoVenta.TRANSFERENCIA_DIGITAL,
                    "EMPRESA",
                    "EcoTienda Gourmet Lima",
                    LocalDateTime.now().minusHours(1),
                    List.of(
                            crearDetalle(coco500, new BigDecimal("5.00"), new BigDecimal("48.00")),
                            crearDetalle(cacao, new BigDecimal("3.00"), new BigDecimal("32.00")),
                            crearDetalle(nibs, new BigDecimal("6.00"), new BigDecimal("18.50"))
                    )
            );

            log.info("Ventas omnicanal demo inicializadas con éxito.");
        }
    }

    private void crearVenta(
            String codigo,
            CanalVenta canal,
            MetodoPagoVenta metodo,
            String clienteTipo,
            String clienteNombre,
            LocalDateTime fecha,
            List<DetalleVenta> detalles
    ) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVenta d : detalles) {
            total = total.add(d.getSubtotal());
        }

        BigDecimal subtotal = total.divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal);

        Venta venta = Venta.builder()
                .codigoVenta(codigo)
                .fechaHora(fecha)
                .canal(canal)
                .clienteTipo(clienteTipo)
                .clienteNombre(clienteNombre)
                .clienteNumeroDoc("00000000")
                .metodoPago(metodo)
                .subtotal(subtotal)
                .igv(igv)
                .total(total)
                .montoPagado(total)
                .vuelto(BigDecimal.ZERO)
                .cajeroNombre("Administrador General")
                .estado(EstadoVenta.EMITIDA)
                .build();

        for (DetalleVenta d : detalles) {
            venta.addDetalle(d);
        }

        ventaRepository.save(venta);
    }

    private DetalleVenta crearDetalle(Producto p, BigDecimal cantidad, BigDecimal precio) {
        return DetalleVenta.builder()
                .productoId(p.getId())
                .productoNombre(p.getNombre())
                .productoImagenUrl(p.getImagenUrl())
                .cantidad(cantidad)
                .precioUnitario(precio)
                .subtotal(cantidad.multiply(precio).setScale(2, RoundingMode.HALF_UP))
                .lotesConsumidosFefo("Lote histórico consumo FEFO")
                .build();
    }
}
