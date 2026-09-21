package pe.edu.upeu.bomerp.finanzas.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import pe.edu.upeu.bomerp.finanzas.entity.CajaChica;
import pe.edu.upeu.bomerp.finanzas.entity.DetalleRendicionCajaChica;
import pe.edu.upeu.bomerp.finanzas.entity.EstadoRendicion;
import pe.edu.upeu.bomerp.finanzas.entity.RendicionCajaChica;
import pe.edu.upeu.bomerp.finanzas.repository.CajaChicaRepository;
import pe.edu.upeu.bomerp.finanzas.repository.RendicionCajaChicaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.init.demo-data", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class FinanzasDataInitializer implements CommandLineRunner {

    private final CajaChicaRepository cajaChicaRepository;
    private final RendicionCajaChicaRepository rendicionRepository;
    private final pe.edu.upeu.bomerp.finanzas.repository.CategoriaFinancieraRepository categoriaFinancieraRepository;
    private final pe.edu.upeu.bomerp.finanzas.repository.TransaccionFinancieraRepository transaccionFinancieraRepository;

    @Override
    public void run(String... args) {
        if (categoriaFinancieraRepository.count() == 0) {
            log.info("Inicializando categorías y transacciones financieras demo...");
            pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera cat1 = new pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera();
            cat1.setNombre("Ventas de Derivados de Cacao");
            cat1.setDescripcion("Ingresos por comercialización de pasta, nibs y manteca");
            cat1.setTipoDefault(pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero.INGRESO);
            categoriaFinancieraRepository.save(cat1);

            pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera cat2 = new pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera();
            cat2.setNombre("Ventas de Aceite de Coco");
            cat2.setDescripcion("Ingresos por venta de aceite de coco extra virgen");
            cat2.setTipoDefault(pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero.INGRESO);
            categoriaFinancieraRepository.save(cat2);

            pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera cat3 = new pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera();
            cat3.setNombre("Insumos y Envases de Producción");
            cat3.setDescripcion("Adquisición de frascos de vidrio, tapas, precintos y etiquetas");
            cat3.setTipoDefault(pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero.EGRESO);
            categoriaFinancieraRepository.save(cat3);

            pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera cat4 = new pe.edu.upeu.bomerp.finanzas.entity.CategoriaFinanciera();
            cat4.setNombre("Servicios Operativos y Logísticos");
            cat4.setDescripcion("Fletes, luz, agua y suministros de almacén");
            cat4.setTipoDefault(pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero.EGRESO);
            categoriaFinancieraRepository.save(cat4);

            // Transacciones Financieras
            pe.edu.upeu.bomerp.finanzas.entity.TransaccionFinanciera trx1 = new pe.edu.upeu.bomerp.finanzas.entity.TransaccionFinanciera();
            trx1.setCodigo("TRX-2026-001");
            trx1.setConcepto("Cobro pedido mayorista ChocoAndes");
            trx1.setMonto(new BigDecimal("1250.00"));
            trx1.setFechaTransaccion(LocalDate.of(2026, 9, 10));
            trx1.setTipo(pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero.INGRESO);
            trx1.setEstado(pe.edu.upeu.bomerp.finanzas.entity.EstadoFinanciero.PAGADO);
            trx1.setTipoComprobante("FACTURA");
            trx1.setNumeroComprobante("F001-00045");
            trx1.setCategoriaFinanciera(cat1);
            transaccionFinancieraRepository.save(trx1);

            pe.edu.upeu.bomerp.finanzas.entity.TransaccionFinanciera trx2 = new pe.edu.upeu.bomerp.finanzas.entity.TransaccionFinanciera();
            trx2.setCodigo("TRX-2026-002");
            trx2.setConcepto("Venta directa en feria de productores Miraflores");
            trx2.setMonto(new BigDecimal("480.00"));
            trx2.setFechaTransaccion(LocalDate.of(2026, 9, 11));
            trx2.setTipo(pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero.INGRESO);
            trx2.setEstado(pe.edu.upeu.bomerp.finanzas.entity.EstadoFinanciero.PAGADO);
            trx2.setTipoComprobante("BOLETA");
            trx2.setNumeroComprobante("B001-00120");
            trx2.setCategoriaFinanciera(cat2);
            transaccionFinancieraRepository.save(trx2);

            pe.edu.upeu.bomerp.finanzas.entity.TransaccionFinanciera trx3 = new pe.edu.upeu.bomerp.finanzas.entity.TransaccionFinanciera();
            trx3.setCodigo("TRX-2026-003");
            trx3.setConcepto("Compra de envases de vidrio 250ml Vidrios del Sur");
            trx3.setMonto(new BigDecimal("350.00"));
            trx3.setFechaTransaccion(LocalDate.of(2026, 9, 12));
            trx3.setTipo(pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero.EGRESO);
            trx3.setEstado(pe.edu.upeu.bomerp.finanzas.entity.EstadoFinanciero.PAGADO);
            trx3.setTipoComprobante("FACTURA");
            trx3.setNumeroComprobante("F002-00891");
            trx3.setCategoriaFinanciera(cat3);
            transaccionFinancieraRepository.save(trx3);

            pe.edu.upeu.bomerp.finanzas.entity.TransaccionFinanciera trx4 = new pe.edu.upeu.bomerp.finanzas.entity.TransaccionFinanciera();
            trx4.setCodigo("TRX-2026-004");
            trx4.setConcepto("Servicio eléctrico planta Enel Lurín");
            trx4.setMonto(new BigDecimal("210.00"));
            trx4.setFechaTransaccion(LocalDate.of(2026, 9, 13));
            trx4.setTipo(pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero.EGRESO);
            trx4.setEstado(pe.edu.upeu.bomerp.finanzas.entity.EstadoFinanciero.PENDIENTE);
            trx4.setTipoComprobante("RECIBO");
            trx4.setNumeroComprobante("R01-9872");
            trx4.setCategoriaFinanciera(cat4);
            transaccionFinancieraRepository.save(trx4);

            pe.edu.upeu.bomerp.finanzas.entity.TransaccionFinanciera trx5 = new pe.edu.upeu.bomerp.finanzas.entity.TransaccionFinanciera();
            trx5.setCodigo("TRX-2026-005");
            trx5.setConcepto("Pedido corporativo EcoTienda (Cobro a 15 días)");
            trx5.setMonto(new BigDecimal("750.00"));
            trx5.setFechaTransaccion(LocalDate.of(2026, 9, 14));
            trx5.setTipo(pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero.INGRESO);
            trx5.setEstado(pe.edu.upeu.bomerp.finanzas.entity.EstadoFinanciero.PENDIENTE);
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
    }
}
