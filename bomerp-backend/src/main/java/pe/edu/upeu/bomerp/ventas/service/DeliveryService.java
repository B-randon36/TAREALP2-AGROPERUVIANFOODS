package pe.edu.upeu.bomerp.ventas.service;

import pe.edu.upeu.bomerp.ventas.dto.ActualizarEstadoDeliveryRequest;
import pe.edu.upeu.bomerp.ventas.dto.AsignarRepartidorRequest;
import pe.edu.upeu.bomerp.ventas.dto.CrearDeliveryRequest;
import pe.edu.upeu.bomerp.ventas.dto.PedidoDeliveryDto;
import pe.edu.upeu.bomerp.ventas.entity.EstadoDelivery;

import java.util.List;

public interface DeliveryService {
    List<PedidoDeliveryDto> listarPedidos(EstadoDelivery estado);
    PedidoDeliveryDto obtenerPorId(Long id);
    PedidoDeliveryDto crearPedido(CrearDeliveryRequest req);
    PedidoDeliveryDto actualizarEstado(Long id, ActualizarEstadoDeliveryRequest req);
    PedidoDeliveryDto asignarRepartidor(Long id, AsignarRepartidorRequest req);
}
