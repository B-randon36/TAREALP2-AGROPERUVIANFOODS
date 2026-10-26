package pe.edu.upeu.bomerp.ventas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.bomerp.ventas.dto.ActualizarEstadoDeliveryRequest;
import pe.edu.upeu.bomerp.ventas.dto.AsignarRepartidorRequest;
import pe.edu.upeu.bomerp.ventas.dto.CrearDeliveryRequest;
import pe.edu.upeu.bomerp.ventas.dto.PedidoDeliveryDto;
import pe.edu.upeu.bomerp.ventas.entity.EstadoDelivery;
import pe.edu.upeu.bomerp.ventas.entity.PedidoDelivery;
import pe.edu.upeu.bomerp.ventas.repository.PedidoDeliveryRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final PedidoDeliveryRepository pedidoDeliveryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDeliveryDto> listarPedidos(EstadoDelivery estado) {
        if (estado != null) {
            return pedidoDeliveryRepository.findByEstadoDeliveryOrderByFechaPactadaAsc(estado).stream()
                    .map(this::toDto)
                    .toList();
        }
        return pedidoDeliveryRepository.findAllByOrderByFechaPactadaDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDeliveryDto obtenerPorId(Long id) {
        return toDto(pedidoDeliveryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido delivery no encontrado: " + id)));
    }

    @Override
    public PedidoDeliveryDto crearPedido(CrearDeliveryRequest req) {
        String codigo = req.getCodigoPedido();
        if (codigo == null || codigo.isBlank()) {
            codigo = "DEL-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" +
                    String.format("%03d", (pedidoDeliveryRepository.count() + 1));
        }

        PedidoDelivery delivery = PedidoDelivery.builder()
                .codigoPedido(codigo)
                .ventaId(req.getVentaId())
                .clienteId(req.getClienteId())
                .clienteNombre(req.getClienteNombre())
                .clienteTelefono(req.getClienteTelefono())
                .direccionEntrega(req.getDireccionEntrega())
                .referencia(req.getReferencia())
                .costoEnvio(req.getCostoEnvio())
                .montoTotal(req.getMontoTotal())
                .repartidorNombre(req.getRepartidorNombre())
                .estadoDelivery(EstadoDelivery.PENDIENTE)
                .fechaPactada(req.getFechaPactada() != null ? req.getFechaPactada() : LocalDateTime.now().plusHours(2))
                .observaciones(req.getObservaciones())
                .build();

        return toDto(pedidoDeliveryRepository.save(delivery));
    }

    @Override
    public PedidoDeliveryDto actualizarEstado(Long id, ActualizarEstadoDeliveryRequest req) {
        PedidoDelivery delivery = pedidoDeliveryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido delivery no encontrado: " + id));

        delivery.setEstadoDelivery(req.getEstado());
        if (req.getEstado() == EstadoDelivery.ENTREGADO) {
            delivery.setFechaEntrega(LocalDateTime.now());
        }
        if (req.getObservaciones() != null && !req.getObservaciones().isBlank()) {
            delivery.setObservaciones((delivery.getObservaciones() != null ? delivery.getObservaciones() + " | " : "") + req.getObservaciones());
        }

        return toDto(pedidoDeliveryRepository.save(delivery));
    }

    @Override
    public PedidoDeliveryDto asignarRepartidor(Long id, AsignarRepartidorRequest req) {
        PedidoDelivery delivery = pedidoDeliveryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido delivery no encontrado: " + id));

        delivery.setRepartidorNombre(req.getRepartidorNombre());
        if (delivery.getEstadoDelivery() == EstadoDelivery.PENDIENTE) {
            delivery.setEstadoDelivery(EstadoDelivery.EN_RUTA);
        }

        return toDto(pedidoDeliveryRepository.save(delivery));
    }

    private PedidoDeliveryDto toDto(PedidoDelivery d) {
        return PedidoDeliveryDto.builder()
                .id(d.getId())
                .codigoPedido(d.getCodigoPedido())
                .ventaId(d.getVentaId())
                .clienteId(d.getClienteId())
                .clienteNombre(d.getClienteNombre())
                .clienteTelefono(d.getClienteTelefono())
                .direccionEntrega(d.getDireccionEntrega())
                .referencia(d.getReferencia())
                .costoEnvio(d.getCostoEnvio())
                .montoTotal(d.getMontoTotal())
                .repartidorNombre(d.getRepartidorNombre())
                .estadoDelivery(d.getEstadoDelivery())
                .fechaPactada(d.getFechaPactada())
                .fechaEntrega(d.getFechaEntrega())
                .observaciones(d.getObservaciones())
                .build();
    }
}
