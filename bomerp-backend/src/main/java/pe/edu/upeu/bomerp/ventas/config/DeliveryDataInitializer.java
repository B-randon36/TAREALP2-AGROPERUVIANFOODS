package pe.edu.upeu.bomerp.ventas.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import pe.edu.upeu.bomerp.ventas.dto.ActualizarEstadoDeliveryRequest;
import pe.edu.upeu.bomerp.ventas.dto.CrearDeliveryRequest;
import pe.edu.upeu.bomerp.ventas.dto.PedidoDeliveryDto;
import pe.edu.upeu.bomerp.ventas.entity.EstadoDelivery;
import pe.edu.upeu.bomerp.ventas.repository.PedidoDeliveryRepository;
import pe.edu.upeu.bomerp.ventas.service.DeliveryService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@Order(35)
@ConditionalOnProperty(name = "app.init.demo-data", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class DeliveryDataInitializer implements CommandLineRunner {

    private final PedidoDeliveryRepository pedidoDeliveryRepository;
    private final DeliveryService deliveryService;

    @Override
    public void run(String... args) {
        if (pedidoDeliveryRepository.count() == 0) {
            log.info("Inicializando pedidos de delivery para reparto a domicilio...");

            LocalDateTime ahora = LocalDateTime.now();

            // Pedido 1: ENTREGADO
            PedidoDeliveryDto ped1 = deliveryService.crearPedido(CrearDeliveryRequest.builder()
                    .codigoPedido("DEL-202604-001")
                    .clienteNombre("Valeria Alarcón Cárdenas")
                    .clienteTelefono("987654321")
                    .direccionEntrega("Av. Angamos Oeste 450, Dpto 402, Miraflores")
                    .referencia("Frente al Parque Tradiciones, timbre 402")
                    .costoEnvio(new BigDecimal("10.00"))
                    .montoTotal(new BigDecimal("140.00"))
                    .repartidorNombre("Carlos Mendoza (Moto 01)")
                    .fechaPactada(ahora.minusHours(3))
                    .observaciones("Pago realizado con Yape. Cliente satisfecho.")
                    .build());

            deliveryService.actualizarEstado(ped1.getId(), ActualizarEstadoDeliveryRequest.builder()
                    .estado(EstadoDelivery.ENTREGADO)
                    .observaciones("Entregado en portería con firma de conformidad.")
                    .build());

            // Pedido 2: EN_RUTA
            PedidoDeliveryDto ped2 = deliveryService.crearPedido(CrearDeliveryRequest.builder()
                    .codigoPedido("DEL-202604-002")
                    .clienteNombre("Restaurante Amazónico Gourmet")
                    .clienteTelefono("991223344")
                    .direccionEntrega("Av. Dos de Mayo 1120, San Isidro")
                    .referencia("Cruce con Calle Los Conquistadores")
                    .costoEnvio(new BigDecimal("12.00"))
                    .montoTotal(new BigDecimal("380.00"))
                    .repartidorNombre("Carlos Mendoza (Moto 01)")
                    .fechaPactada(ahora.plusMinutes(45))
                    .observaciones("Frágil: incluye frascos de vidrio de aceite de coco 1L.")
                    .build());

            deliveryService.actualizarEstado(ped2.getId(), ActualizarEstadoDeliveryRequest.builder()
                    .estado(EstadoDelivery.EN_RUTA)
                    .observaciones("Motorizado salió de almacén central a las " + ahora.toLocalTime())
                    .build());

            // Pedido 3: PENDIENTE
            deliveryService.crearPedido(CrearDeliveryRequest.builder()
                    .codigoPedido("DEL-202604-003")
                    .clienteNombre("Tienda Natural BioPerú (Surco)")
                    .clienteTelefono("945112233")
                    .direccionEntrega("Av. Alfredo Benavides 2890, Santiago de Surco")
                    .referencia("Al costado de la estación del tren")
                    .costoEnvio(new BigDecimal("8.00"))
                    .montoTotal(new BigDecimal("215.00"))
                    .fechaPactada(ahora.plusHours(2))
                    .observaciones("Llevar POS inalámbrico para cobro contraentrega.")
                    .build());

            // Pedido 4: PENDIENTE
            deliveryService.crearPedido(CrearDeliveryRequest.builder()
                    .codigoPedido("DEL-202604-004")
                    .clienteNombre("Familia Quispe Ramos")
                    .clienteTelefono("978334455")
                    .direccionEntrega("Calle Las Palmeras 340, Los Olivos")
                    .referencia("Altura de MegaPlaza, rejas blancas")
                    .costoEnvio(new BigDecimal("15.00"))
                    .montoTotal(new BigDecimal("85.00"))
                    .fechaPactada(ahora.plusHours(4))
                    .observaciones("Entrega programada en horario vespertino.")
                    .build());

            log.info("Pedidos de delivery inicializados con éxito.");
        }
    }
}
