package pe.edu.upeu.bomerp.compras.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import pe.edu.upeu.bomerp.compras.dto.ItemCompraRequest;
import pe.edu.upeu.bomerp.compras.dto.RegistrarCompraRequest;
import pe.edu.upeu.bomerp.compras.dto.TerceroCreateRequest;
import pe.edu.upeu.bomerp.compras.dto.TerceroDto;
import pe.edu.upeu.bomerp.compras.entity.TipoTercero;
import pe.edu.upeu.bomerp.compras.repository.CompraRepository;
import pe.edu.upeu.bomerp.compras.repository.TerceroRepository;
import pe.edu.upeu.bomerp.compras.service.CompraService;
import pe.edu.upeu.bomerp.compras.service.TerceroService;
import pe.edu.upeu.bomerp.produccion.dto.InsumoDto;
import pe.edu.upeu.bomerp.produccion.service.InsumoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Configuration
@Order(16)
@ConditionalOnProperty(name = "app.init.demo-data", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class ComprasDataInitializer implements CommandLineRunner {

    private final TerceroRepository terceroRepository;
    private final CompraRepository compraRepository;
    private final TerceroService terceroService;
    private final CompraService compraService;
    private final InsumoService insumoService;

    @Override
    public void run(String... args) {
        if (terceroRepository.count() == 0) {
            log.info("Inicializando catálogo de Terceros (Clientes y Proveedores) y Compras demo...");

            // 1. Proveedores
            TerceroDto provVidrios = terceroService.crear(TerceroCreateRequest.builder()
                    .tipoDocumento("RUC")
                    .numeroDocumento("20512345678")
                    .razonSocial("Vidrios Industriales del Perú S.A.C.")
                    .tipoTercero(TipoTercero.PROVEEDOR)
                    .telefono("01-4458920")
                    .direccion("Av. Materiales 1240, Callao")
                    .correo("ventas@vidriosperu.com.pe")
                    .build());

            TerceroDto provEmpaques = terceroService.crear(TerceroCreateRequest.builder()
                    .tipoDocumento("RUC")
                    .numeroDocumento("20601234567")
                    .razonSocial("Etiquetas & Empaques del Centro")
                    .tipoTercero(TipoTercero.PROVEEDOR)
                    .telefono("01-3267890")
                    .direccion("Jr. Los Sauces 450, Ate Vitarte, Lima")
                    .correo("contacto@empaquesdelcentro.pe")
                    .build());

            TerceroDto provCacao = terceroService.crear(TerceroCreateRequest.builder()
                    .tipoDocumento("RUC")
                    .numeroDocumento("20456789012")
                    .razonSocial("Asociación de Productores de Cacao San Martín")
                    .tipoTercero(TipoTercero.PROVEEDOR)
                    .telefono("042-523411")
                    .direccion("Carretera Fernando Belaúnde Km 14, Tarapoto")
                    .correo("acopio@cacaosanmartin.org.pe")
                    .build());

            // 2. Clientes
            terceroService.crear(TerceroCreateRequest.builder()
                    .tipoDocumento("RUC")
                    .numeroDocumento("20100070970")
                    .razonSocial("Supermercados Peruanos S.A.")
                    .tipoTercero(TipoTercero.CLIENTE)
                    .telefono("01-6188000")
                    .direccion("Calle Morelli 181, San Borja, Lima")
                    .correo("proveedores@spsa.com.pe")
                    .build());

            terceroService.crear(TerceroCreateRequest.builder()
                    .tipoDocumento("RUC")
                    .numeroDocumento("20498765432")
                    .razonSocial("Distribuidora San Jorge E.I.R.L.")
                    .tipoTercero(TipoTercero.CLIENTE)
                    .telefono("01-4721990")
                    .direccion("Av. Iquitos 890, La Victoria, Lima")
                    .correo("ventas@sanjorgeeirl.com")
                    .build());

            terceroService.crear(TerceroCreateRequest.builder()
                    .tipoDocumento("RUC")
                    .numeroDocumento("20556677889")
                    .razonSocial("Ecomercado Orgánico Miraflores")
                    .tipoTercero(TipoTercero.CLIENTE)
                    .telefono("01-2415533")
                    .direccion("Av. Larco 780, Miraflores, Lima")
                    .correo("administracion@ecomercado.pe")
                    .build());

            // 3. Compras Históricas con insumos
            List<InsumoDto> insumos = insumoService.listarTodos();
            if (!insumos.isEmpty()) {
                InsumoDto frasco = insumos.stream()
                        .filter(i -> i.getCodigo().contains("FRAS"))
                        .findFirst()
                        .orElse(insumos.get(0));

                InsumoDto tapa = insumos.stream()
                        .filter(i -> i.getCodigo().contains("TAPA"))
                        .findFirst()
                        .orElse(insumos.get(0));

                InsumoDto materia = insumos.stream()
                        .filter(i -> i.getCodigo().contains("COC") || i.getCodigo().contains("CACA"))
                        .findFirst()
                        .orElse(insumos.get(0));

                // Compra 1: Frascos y tapas
                compraService.registrarCompra(RegistrarCompraRequest.builder()
                        .codigoCompra("COM-202604-0001")
                        .proveedorId(provVidrios.getId())
                        .numeroComprobante("F001-000452")
                        .fechaCompra(LocalDate.now().minusDays(10))
                        .metodoPago("TRANSFERENCIA")
                        .observaciones("Abastecimiento mensual de envases de vidrio para envasado de coco")
                        .items(List.of(
                                ItemCompraRequest.builder().insumoId(frasco.getId()).cantidad(new BigDecimal("300.00")).precioUnitario(new BigDecimal("1.8500")).build(),
                                ItemCompraRequest.builder().insumoId(tapa.getId()).cantidad(new BigDecimal("500.00")).precioUnitario(new BigDecimal("0.4200")).build()
                        ))
                        .build());

                // Compra 2: Materia prima de cacao
                compraService.registrarCompra(RegistrarCompraRequest.builder()
                        .codigoCompra("COM-202604-0002")
                        .proveedorId(provCacao.getId())
                        .numeroComprobante("F002-001289")
                        .fechaCompra(LocalDate.now().minusDays(3))
                        .metodoPago("EFECTIVO")
                        .observaciones("Lote de granos secos seleccionado de cooperativa asociada")
                        .items(List.of(
                                ItemCompraRequest.builder().insumoId(materia.getId()).cantidad(new BigDecimal("200.00")).precioUnitario(new BigDecimal("7.9000")).build()
                        ))
                        .build());

                log.info("Compras demo inicializadas correctamente.");
            }
        }
    }
}
