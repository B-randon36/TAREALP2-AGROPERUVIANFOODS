package pe.edu.upeu.bomerp.finanzas.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import pe.edu.upeu.bomerp.finanzas.entity.*;
import pe.edu.upeu.bomerp.finanzas.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.init.demo-data", havingValue = "true", matchIfMissing = true)
public class FinanzasDataInitializer implements CommandLineRunner {

    private final CajaChicaRepository cajaChicaRepository;
    private final RendicionCajaChicaRepository rendicionRepository;
    private final CategoriaFinancieraRepository categoriaFinancieraRepository;
    private final TransaccionFinancieraRepository transaccionFinancieraRepository;
    private final SesionCajaRepository sesionCajaRepository;

    public FinanzasDataInitializer(
            CajaChicaRepository cajaChicaRepository,
            RendicionCajaChicaRepository rendicionRepository,
            CategoriaFinancieraRepository categoriaFinancieraRepository,
            TransaccionFinancieraRepository transaccionFinancieraRepository,
            SesionCajaRepository sesionCajaRepository) {
        this.cajaChicaRepository = cajaChicaRepository;
        this.rendicionRepository = rendicionRepository;
        this.categoriaFinancieraRepository = categoriaFinancieraRepository;
        this.transaccionFinancieraRepository = transaccionFinancieraRepository;
        this.sesionCajaRepository = sesionCajaRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaFinancieraRepository.count() == 0) {
            log.info("Inicializando categorías y transacciones financieras demo...");
            CategoriaFinanciera cat1 = new CategoriaFinanciera();
            cat1.setNombre("Ventas de Derivados de Cacao");
            cat1.setDescripcion("Ingresos por comercialización de pasta, nibs y manteca");
            cat1.setTipoDefault(TipoFinanciero.INGRESO);
            categoriaFinancieraRepository.save(cat1);

            CategoriaFinanciera cat2 = new CategoriaFinanciera();
            cat2.setNombre("Ventas de Aceite de Coco");
            cat2.setDescripcion("Ingresos por venta de aceite de coco extra virgen");
            cat2.setTipoDefault(TipoFinanciero.INGRESO);
            categoriaFinancieraRepository.save(cat2);

            CategoriaFinanciera cat3 = new CategoriaFinanciera();
            cat3.setNombre("Insumos y Envases de Producción");
            cat3.setDescripcion("Adquisición de frascos de vidrio, tapas, precintos y etiquetas");
            cat3.setTipoDefault(TipoFinanciero.EGRESO);
            categoriaFinancieraRepository.save(cat3);

            CategoriaFinanciera cat4 = new CategoriaFinanciera();
            cat4.setNombre("Servicios Operativos y Logísticos");
            cat4.setDescripcion("Fletes, luz, agua y suministros de almacén");
            cat4.setTipoDefault(TipoFinanciero.EGRESO);
            categoriaFinancieraRepository.save(cat4);

            // Transacciones Financieras
            TransaccionFinanciera trx1 = new TransaccionFinanciera();
            trx1.setCodigo("TRX-2026-001");
            trx1.setConcepto("Cobro pedido mayorista ChocoAndes");
            trx1.setMonto(new BigDecimal("1250.00"));
            trx1.setFechaTransaccion(LocalDate.of(2026, 9, 10));
            trx1.setTipo(TipoFinanciero.INGRESO);
            trx1.setEstado(EstadoFinanciero.PAGADO);
            trx1.setTipoComprobante("FACTURA");
            trx1.setNumeroComprobante("F001-00045");
            trx1.setCategoriaFinanciera(cat1);
            transaccionFinancieraRepository.save(trx1);

            TransaccionFinanciera trx2 = new TransaccionFinanciera();
            trx2.setCodigo("TRX-2026-002");
            trx2.setConcepto("Venta directa en feria de productores Miraflores");
            trx2.setMonto(new BigDecimal("480.00"));
            trx2.setFechaTransaccion(LocalDate.of(2026, 9, 11));
            trx2.setTipo(TipoFinanciero.INGRESO);
            trx2.setEstado(EstadoFinanciero.PAGADO);
            trx2.setTipoComprobante("BOLETA");
            trx2.setNumeroComprobante("B001-00120");
            trx2.setCategoriaFinanciera(cat2);
            transaccionFinancieraRepository.save(trx2);

            TransaccionFinanciera trx3 = new TransaccionFinanciera();
            trx3.setCodigo("TRX-2026-003");
            trx3.setConcepto("Compra de envases de vidrio 250ml Vidrios del Sur");
            trx3.setMonto(new BigDecimal("350.00"));
            trx3.setFechaTransaccion(LocalDate.of(2026, 9, 12));
            trx3.setTipo(TipoFinanciero.EGRESO);
            trx3.setEstado(EstadoFinanciero.PAGADO);
            trx3.setTipoComprobante("FACTURA");
            trx3.setNumeroComprobante("F002-00891");
            trx3.setCategoriaFinanciera(cat3);
            transaccionFinancieraRepository.save(trx3);

            TransaccionFinanciera trx4 = new TransaccionFinanciera();
            trx4.setCodigo("TRX-2026-004");
            trx4.setConcepto("Servicio eléctrico planta Enel Lurín");
            trx4.setMonto(new BigDecimal("210.00"));
            trx4.setFechaTransaccion(LocalDate.of(2026, 9, 13));
            trx4.setTipo(TipoFinanciero.EGRESO);
            trx4.setEstado(EstadoFinanciero.PENDIENTE);
            trx4.setTipoComprobante("RECIBO");
            trx4.setNumeroComprobante("R01-9872");
            trx4.setCategoriaFinanciera(cat4);
            transaccionFinancieraRepository.save(trx4);

            TransaccionFinanciera trx5 = new TransaccionFinanciera();
            trx5.setCodigo("TRX-2026-005");
            trx5.setConcepto("Pedido corporativo EcoTienda (Cobro a 15 días)");
            trx5.setMonto(new BigDecimal("750.00"));
            trx5.setFechaTransaccion(LocalDate.of(2026, 9, 14));
            trx5.setTipo(TipoFinanciero.INGRESO);
            trx5.setEstado(EstadoFinanciero.PENDIENTE);
            trx5.setTipoComprobante("FACTURA");
            trx5.setNumeroComprobante("F001-00049");
            trx5.setCategoriaFinanciera(cat1);
            transaccionFinancieraRepository.save(trx5);
        }

        if (cajaChicaRepository.count() == 0) {
            log.info("Inicializando datos demo para el módulo de Finanzas...");

            CajaChica caja1 = CajaChica.builder()
                    .codigo("CCH-001")
                    .responsable("Brandon Ccalla")
                    .limiteMaximo(new BigDecimal("1000.00"))
                    .saldoDisponible(new BigDecimal("475.00"))
                    .build();

            CajaChica caja2 = CajaChica.builder()
                    .codigo("CCH-002")
                    .responsable("Isai Armuto")
                    .limiteMaximo(new BigDecimal("500.00"))
                    .saldoDisponible(new BigDecimal("500.00"))
                    .build();

            cajaChicaRepository.save(caja1);
            cajaChicaRepository.save(caja2);

            // Rendición 1: Aprobada - S/ 150.00
            RendicionCajaChica rnd1 = RendicionCajaChica.builder()
                    .codigo("RND-2026-001")
                    .responsable("Brandon Ccalla")
                    .fecha(LocalDate.of(2026, 9, 10))
                    .estado(EstadoRendicion.APROBADO)
                    .cajaChica(caja1)
                    .build();
            rnd1.agregarDetalle(DetalleRendicionCajaChica.builder()
                    .concepto("Envases de vidrio para cacao")
                    .cantidad(2)
                    .precioUnitario(new BigDecimal("50.00"))
                    .subtotal(new BigDecimal("100.00"))
                    .build());
            rnd1.agregarDetalle(DetalleRendicionCajaChica.builder()
                    .concepto("Cintas de embalaje")
                    .cantidad(1)
                    .precioUnitario(new BigDecimal("50.00"))
                    .subtotal(new BigDecimal("50.00"))
                    .build());
            rnd1.calcularTotal();
            rendicionRepository.save(rnd1);

            // Rendición 2: Aprobada - S/ 300.00
            RendicionCajaChica rnd2 = RendicionCajaChica.builder()
                    .codigo("RND-2026-002")
                    .responsable("Brandon Ccalla")
                    .fecha(LocalDate.of(2026, 9, 12))
                    .estado(EstadoRendicion.APROBADO)
                    .cajaChica(caja1)
                    .build();
            rnd2.agregarDetalle(DetalleRendicionCajaChica.builder()
                    .concepto("Combustible furgón despacho")
                    .cantidad(1)
                    .precioUnitario(new BigDecimal("300.00"))
                    .subtotal(new BigDecimal("300.00"))
                    .build());
            rnd2.calcularTotal();
            rendicionRepository.save(rnd2);

            // Rendición 3: Pendiente - S/ 75.00
            RendicionCajaChica rnd3 = RendicionCajaChica.builder()
                    .codigo("RND-2026-003")
                    .responsable("Zaggy Morales")
                    .fecha(LocalDate.of(2026, 9, 13))
                    .estado(EstadoRendicion.PENDIENTE)
                    .cajaChica(caja1)
                    .build();
            rnd3.agregarDetalle(DetalleRendicionCajaChica.builder()
                    .concepto("Papelería y notas de venta")
                    .cantidad(1)
                    .precioUnitario(new BigDecimal("75.00"))
                    .subtotal(new BigDecimal("75.00"))
                    .build());
            rnd3.calcularTotal();
            rendicionRepository.save(rnd3);

            log.info("Datos demo de Finanzas cargados exitosamente (3 rendiciones registradas).");
        }

        if (sesionCajaRepository.count() == 0) {
            log.info("Inicializando Sesión de Caja Diaria Demo (Agro Peruvian Foods)...");
            CategoriaFinanciera catVentas = categoriaFinancieraRepository.findAll().stream()
                    .filter(c -> c.getTipoDefault() == TipoFinanciero.INGRESO)
                    .findFirst().orElse(null);

            CategoriaFinanciera catGastos = categoriaFinancieraRepository.findAll().stream()
                    .filter(c -> c.getTipoDefault() == TipoFinanciero.EGRESO)
                    .findFirst().orElse(null);

            SesionCaja sesion = SesionCaja.builder()
                    .codigoSesion("SES-20261018-001")
                    .usuarioId(1L)
                    .cajero("Brandon Ccalla")
                    .fechaApertura(LocalDateTime.now().minusHours(4))
                    .saldoInicial(new BigDecimal("350.00"))
                    .totalIngresosEfectivo(BigDecimal.ZERO)
                    .totalEgresosEfectivo(BigDecimal.ZERO)
                    .saldoEsperadoEfectivo(new BigDecimal("350.00"))
                    .estado(EstadoCaja.ABIERTA)
                    .build();

            if (catVentas != null) {
                sesion.agregarMovimiento(MovimientoCaja.builder()
                        .fechaHora(LocalDateTime.now().minusHours(3))
                        .tipo(TipoFinanciero.INGRESO)
                        .metodoPago(MetodoPago.EFECTIVO)
                        .monto(new BigDecimal("120.00"))
                        .descripcion("Venta 3 frascos Aceite de Coco 500ml")
                        .referenciaExterna("VTA-00101")
                        .categoriaFinanciera(catVentas)
                        .build());

                sesion.agregarMovimiento(MovimientoCaja.builder()
                        .fechaHora(LocalDateTime.now().minusHours(2))
                        .tipo(TipoFinanciero.INGRESO)
                        .metodoPago(MetodoPago.YAPE)
                        .monto(new BigDecimal("85.00"))
                        .descripcion("Venta Pack Chocolates 70% Cacao")
                        .referenciaExterna("VTA-00102")
                        .categoriaFinanciera(catVentas)
                        .build());
            }

            if (catGastos != null) {
                sesion.agregarMovimiento(MovimientoCaja.builder()
                        .fechaHora(LocalDateTime.now().minusHours(1))
                        .tipo(TipoFinanciero.EGRESO)
                        .metodoPago(MetodoPago.EFECTIVO)
                        .monto(new BigDecimal("35.00"))
                        .descripcion("Compra de bolsas kraft para despacho")
                        .referenciaExterna("EGR-00045")
                        .categoriaFinanciera(catGastos)
                        .build());
            }

            sesionCajaRepository.save(sesion);
            log.info("Sesión de caja demo inicializada con código: {}", sesion.getCodigoSesion());
        }
    }
}
