package pe.edu.upeu.bomerp.compras.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.compras.dto.CompraDto;
import pe.edu.upeu.bomerp.compras.dto.RegistrarCompraRequest;
import pe.edu.upeu.bomerp.compras.service.CompraService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compras")
@RequiredArgsConstructor
@Tag(name = "Compras - Órdenes de Compra", description = "Abastecimiento de insumos y materiales con actualización de stock")
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    @Operation(summary = "Listar historial de compras")
    public ResponseEntity<List<CompraDto>> listarTodas() {
        return ResponseEntity.ok(compraService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de compra por ID")
    public ResponseEntity<CompraDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nueva compra de insumos")
    public ResponseEntity<CompraDto> registrar(@Valid @RequestBody RegistrarCompraRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.registrarCompra(req));
    }

    @PatchMapping("/{id}/recibir")
    @Operation(summary = "Marcar compra como recibida e ingresar insumos a almacén")
    public ResponseEntity<CompraDto> recibir(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.recibirCompra(id));
    }

    @PatchMapping("/{id}/anular")
    @Operation(summary = "Anular compra y revertir stock")
    public ResponseEntity<CompraDto> anular(@PathVariable Long id, @RequestParam(defaultValue = "Anulación por usuario") String motivo) {
        return ResponseEntity.ok(compraService.anularCompra(id, motivo));
    }
}
