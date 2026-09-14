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

    @Override
    public void run(String... args) {
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
