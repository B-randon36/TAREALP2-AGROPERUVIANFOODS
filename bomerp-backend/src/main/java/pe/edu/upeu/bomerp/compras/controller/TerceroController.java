package pe.edu.upeu.bomerp.compras.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.compras.dto.TerceroCreateRequest;
import pe.edu.upeu.bomerp.compras.dto.TerceroDto;
import pe.edu.upeu.bomerp.compras.entity.TipoTercero;
import pe.edu.upeu.bomerp.compras.service.TerceroService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/terceros")
@RequiredArgsConstructor
@Tag(name = "Compras & CRM - Terceros", description = "Directorio de Clientes y Proveedores de Agro Peruvian Foods")
public class TerceroController {

    private final TerceroService terceroService;

    @GetMapping
    @Operation(summary = "Listar todos los terceros activos")
    public ResponseEntity<List<TerceroDto>> listarTodos(@RequestParam(required = false) TipoTercero tipo) {
        if (tipo != null) {
            return ResponseEntity.ok(terceroService.listarPorTipo(tipo));
        }
        return ResponseEntity.ok(terceroService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un tercero por ID")
    public ResponseEntity<TerceroDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(terceroService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo cliente o proveedor")
    public ResponseEntity<TerceroDto> crear(@Valid @RequestBody TerceroCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(terceroService.crear(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar información de tercero")
    public ResponseEntity<TerceroDto> actualizar(@PathVariable Long id, @Valid @RequestBody TerceroCreateRequest req) {
        return ResponseEntity.ok(terceroService.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar tercero")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        terceroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
