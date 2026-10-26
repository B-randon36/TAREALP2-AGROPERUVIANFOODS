package pe.edu.upeu.bomerp.ventas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.ventas.dto.ActualizarEstadoDeliveryRequest;
import pe.edu.upeu.bomerp.ventas.dto.AsignarRepartidorRequest;
import pe.edu.upeu.bomerp.ventas.dto.CrearDeliveryRequest;
import pe.edu.upeu.bomerp.ventas.dto.PedidoDeliveryDto;
import pe.edu.upeu.bomerp.ventas.entity.EstadoDelivery;
import pe.edu.upeu.bomerp.ventas.service.DeliveryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas/delivery")
@RequiredArgsConstructor
@Tag(name = "Ventas - Delivery & Reparto", description = "Despachos y monitoreo logístico de envíos a domicilio")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    @Operation(summary = "Listar pedidos de delivery con filtro opcional de estado")
    public ResponseEntity<List<PedidoDeliveryDto>> listarPedidos(@RequestParam(required = false) EstadoDelivery estado) {
        return ResponseEntity.ok(deliveryService.listarPedidos(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un pedido de reparto por ID")
    public ResponseEntity<PedidoDeliveryDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo pedido de reparto a domicilio")
    public ResponseEntity<PedidoDeliveryDto> crearPedido(@Valid @RequestBody CrearDeliveryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.crearPedido(req));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de reparto (PENDIENTE, EN_RUTA, ENTREGADO, CANCELADO)")
    public ResponseEntity<PedidoDeliveryDto> actualizarEstado(@PathVariable Long id, @Valid @RequestBody ActualizarEstadoDeliveryRequest req) {
        return ResponseEntity.ok(deliveryService.actualizarEstado(id, req));
    }

    @PatchMapping("/{id}/repartidor")
    @Operation(summary = "Asignar conductor o motorizado de reparto")
    public ResponseEntity<PedidoDeliveryDto> asignarRepartidor(@PathVariable Long id, @Valid @RequestBody AsignarRepartidorRequest req) {
        return ResponseEntity.ok(deliveryService.asignarRepartidor(id, req));
    }
}
