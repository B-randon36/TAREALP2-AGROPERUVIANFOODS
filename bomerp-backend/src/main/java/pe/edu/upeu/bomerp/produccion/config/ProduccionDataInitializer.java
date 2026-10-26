package pe.edu.upeu.bomerp.produccion.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import pe.edu.upeu.bomerp.catalogo.producto.repository.ProductoRepository;
import pe.edu.upeu.bomerp.produccion.dto.CompletarOrdenRequest;
import pe.edu.upeu.bomerp.produccion.dto.CrearOrdenRequest;
import pe.edu.upeu.bomerp.produccion.dto.InsumoCreateRequest;
import pe.edu.upeu.bomerp.produccion.dto.InsumoDto;
import pe.edu.upeu.bomerp.produccion.dto.ItemRecetaRequest;
import pe.edu.upeu.bomerp.produccion.dto.OrdenProduccionDto;
import pe.edu.upeu.bomerp.produccion.dto.RecetaBomCreateRequest;
import pe.edu.upeu.bomerp.produccion.dto.RecetaBomDto;
import pe.edu.upeu.bomerp.produccion.entity.UnidadMedida;
import pe.edu.upeu.bomerp.produccion.repository.InsumoRepository;
import pe.edu.upeu.bomerp.produccion.repository.OrdenProduccionRepository;
import pe.edu.upeu.bomerp.produccion.service.InsumoService;
import pe.edu.upeu.bomerp.produccion.service.OrdenProduccionService;
import pe.edu.upeu.bomerp.produccion.service.RecetaBomService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Configuration
@Order(15)
@ConditionalOnProperty(name = "app.init.demo-data", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class ProduccionDataInitializer implements CommandLineRunner {

    private final InsumoRepository insumoRepository;
    private final OrdenProduccionRepository ordenProduccionRepository;
    private final ProductoRepository productoRepository;
    private final InsumoService insumoService;
    private final RecetaBomService recetaBomService;
    private final OrdenProduccionService ordenProduccionService;

    @Override
    public void run(String... args) {
        if (insumoRepository.count() == 0) {
            log.info("Inicializando datos demo de Producción, Insumos y Recetas BOM...");

            // 1. Insumos
            InsumoDto frasco1L = insumoService.crear(InsumoCreateRequest.builder()
                    .codigo("INS-FRAS-1L")
                    .nombre("Frascos de vidrio 1L con boca ancha")
                    .unidadMedida(UnidadMedida.UNIDAD)
                    .costoUnitario(new BigDecimal("2.8000"))
                    .stockActual(new BigDecimal("600.00"))
                    .stockMinimo(new BigDecimal("100.00"))
                    .build());

            InsumoDto frasco500 = insumoService.crear(InsumoCreateRequest.builder()
                    .codigo("INS-FRAS-500")
                    .nombre("Frascos de vidrio 500ml con rosca")
                    .unidadMedida(UnidadMedida.UNIDAD)
                    .costoUnitario(new BigDecimal("1.9000"))
                    .stockActual(new BigDecimal("800.00"))
                    .stockMinimo(new BigDecimal("150.00"))
                    .build());

            InsumoDto tapaDor = insumoService.crear(InsumoCreateRequest.builder()
                    .codigo("INS-TAPA-DOR")
                    .nombre("Tapas doradas herméticas con plastisol")
                    .unidadMedida(UnidadMedida.UNIDAD)
                    .costoUnitario(new BigDecimal("0.4500"))
                    .stockActual(new BigDecimal("2000.00"))
                    .stockMinimo(new BigDecimal("300.00"))
                    .build());

            InsumoDto etiqFro = insumoService.crear(InsumoCreateRequest.builder()
                    .codigo("INS-ETIQ-FRO")
                    .nombre("Etiquetas frontales plastificadas mate")
                    .unidadMedida(UnidadMedida.UNIDAD)
                    .costoUnitario(new BigDecimal("0.3500"))
                    .stockActual(new BigDecimal("2500.00"))
                    .stockMinimo(new BigDecimal("400.00"))
                    .build());

            InsumoDto precinto = insumoService.crear(InsumoCreateRequest.builder()
                    .codigo("INS-PREC-SEG")
                    .nombre("Precintos termoencogibles de seguridad")
                    .unidadMedida(UnidadMedida.UNIDAD)
                    .costoUnitario(new BigDecimal("0.1500"))
                    .stockActual(new BigDecimal("3000.00"))
                    .stockMinimo(new BigDecimal("500.00"))
                    .build());

            InsumoDto copraCoco = insumoService.crear(InsumoCreateRequest.builder()
                    .codigo("INS-COPR-COC")
                    .nombre("Copra de coco orgánico seleccionada")
                    .unidadMedida(UnidadMedida.KG)
                    .costoUnitario(new BigDecimal("4.5000"))
                    .stockActual(new BigDecimal("1500.00"))
                    .stockMinimo(new BigDecimal("300.00"))
                    .build());

            InsumoDto granoCacao = insumoService.crear(InsumoCreateRequest.builder()
                    .codigo("INS-CACA-GRAN")
                    .nombre("Granos de cacao fermentado San Martín (Chuncho)")
                    .unidadMedida(UnidadMedida.KG)
                    .costoUnitario(new BigDecimal("8.2000"))
                    .stockActual(new BigDecimal("1200.00"))
                    .stockMinimo(new BigDecimal("250.00"))
                    .build());

            InsumoDto doyPack = insumoService.crear(InsumoCreateRequest.builder()
                    .codigo("INS-DOY-250")
                    .nombre("Bolsas doy-pack kraft trilaminadas 250g")
                    .unidadMedida(UnidadMedida.UNIDAD)
                    .costoUnitario(new BigDecimal("0.8500"))
                    .stockActual(new BigDecimal("1000.00"))
                    .stockMinimo(new BigDecimal("200.00"))
                    .build());

            // 2. Recetas BOM vinculadas a productos de catálogo
            List<Producto> productos = productoRepository.findAll();
            if (!productos.isEmpty()) {
                Producto prodCoco = productos.stream()
                        .filter(p -> p.getNombre().toLowerCase().contains("coco"))
                        .findFirst()
                        .orElse(productos.get(0));

                RecetaBomDto recetaCoco = recetaBomService.crear(RecetaBomCreateRequest.builder()
                        .codigoReceta("BOM-COCO-001")
                        .productoId(prodCoco.getId())
                        .nombreVersion("Receta Estándar Aceite de Coco v1.0")
                        .costoManoObraEstimado(new BigDecimal("3.50"))
                        .items(List.of(
                                ItemRecetaRequest.builder().insumoId(copraCoco.getId()).cantidadRequerida(new BigDecimal("1.8000")).build(),
                                ItemRecetaRequest.builder().insumoId(frasco500.getId()).cantidadRequerida(new BigDecimal("1.0000")).build(),
                                ItemRecetaRequest.builder().insumoId(tapaDor.getId()).cantidadRequerida(new BigDecimal("1.0000")).build(),
                                ItemRecetaRequest.builder().insumoId(etiqFro.getId()).cantidadRequerida(new BigDecimal("1.0000")).build(),
                                ItemRecetaRequest.builder().insumoId(precinto.getId()).cantidadRequerida(new BigDecimal("1.0000")).build()
                        ))
                        .build());

                Producto prodCacao = productos.stream()
                        .filter(p -> p.getNombre().toLowerCase().contains("cacao"))
                        .findFirst()
                        .orElse(productos.get(productos.size() - 1));

                RecetaBomDto recetaCacao = recetaBomService.crear(RecetaBomCreateRequest.builder()
                        .codigoReceta("BOM-CACA-002")
                        .productoId(prodCacao.getId())
                        .nombreVersion("Receta Artesanal Nibs y Pasta v1.0")
                        .costoManoObraEstimado(new BigDecimal("2.50"))
                        .items(List.of(
                                ItemRecetaRequest.builder().insumoId(granoCacao.getId()).cantidadRequerida(new BigDecimal("0.3500")).build(),
                                ItemRecetaRequest.builder().insumoId(doyPack.getId()).cantidadRequerida(new BigDecimal("1.0000")).build(),
                                ItemRecetaRequest.builder().insumoId(etiqFro.getId()).cantidadRequerida(new BigDecimal("1.0000")).build()
                        ))
                        .build());

                // 3. Crear Órdenes de Producción
                // Orden 1: Completada con lote generado
                OrdenProduccionDto orden1 = ordenProduccionService.crearOrden(CrearOrdenRequest.builder()
                        .codigoOrden("OP-2026-001")
                        .recetaBomId(recetaCoco.getId())
                        .cantidadAProducir(new BigDecimal("50.00"))
                        .observaciones("Producción de lote inicial para distribución tiendas San Isidro")
                        .build());

                ordenProduccionService.completarOrden(orden1.getId(), CompletarOrdenRequest.builder()
                        .observaciones("Control de calidad conforme. Envasado al vacío verificado.")
                        .build());

                // Orden 2: Planificada en proceso
                ordenProduccionService.crearOrden(CrearOrdenRequest.builder()
                        .codigoOrden("OP-2026-002")
                        .recetaBomId(recetaCacao.getId())
                        .cantidadAProducir(new BigDecimal("80.00"))
                        .observaciones("Programada para turno tarde en planta Lurín")
                        .build());

                log.info("Datos demo de producción inicializados con éxito.");
            }
        }
    }
}
