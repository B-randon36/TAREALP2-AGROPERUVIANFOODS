package pe.edu.upeu.bomerp.produccion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.produccion.dto.CompletarOrdenRequest;
import pe.edu.upeu.bomerp.produccion.dto.CrearOrdenRequest;
import pe.edu.upeu.bomerp.produccion.dto.OrdenProduccionDto;
import pe.edu.upeu.bomerp.produccion.service.OrdenProduccionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/produccion/ordenes")
@RequiredArgsConstructor
@Tag(name = "Producción - Órdenes de Trabajo", description = "Gestión del flujo de producción y generación automática de lotes")
public class OrdenProduccionController {

    private final OrdenProduccionService ordenProduccionService;

    @GetMapping
    @Operation(summary = "Listar órdenes de producción")
    public ResponseEntity<List<OrdenProduccionDto>> listarTodas() {
        return ResponseEntity.ok(ordenProduccionService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de orden de producción")
    public ResponseEntity<OrdenProduccionDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenProduccionService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Planificar nueva orden de producción")
    public ResponseEntity<OrdenProduccionDto> crear(@Valid @RequestBody CrearOrdenRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenProduccionService.crearOrden(req));
    }

    @PatchMapping("/{id}/completar")
    @Operation(summary = "Completar orden, consumir insumos y generar lote en inventario")
    public ResponseEntity<OrdenProduccionDto> completar(@PathVariable Long id, @RequestBody(required = false) CompletarOrdenRequest req) {
        return ResponseEntity.ok(ordenProduccionService.completarOrden(id, req));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar orden de producción")
    public ResponseEntity<OrdenProduccionDto> cancelar(@PathVariable Long id, @RequestParam(defaultValue = "Cancelación por usuario") String motivo) {
        return ResponseEntity.ok(ordenProduccionService.cancelarOrden(id, motivo));
    }
}
