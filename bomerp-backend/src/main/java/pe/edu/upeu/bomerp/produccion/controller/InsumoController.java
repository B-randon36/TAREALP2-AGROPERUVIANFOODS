package pe.edu.upeu.bomerp.produccion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.produccion.dto.InsumoCreateRequest;
import pe.edu.upeu.bomerp.produccion.dto.InsumoDto;
import pe.edu.upeu.bomerp.produccion.service.InsumoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/produccion/insumos")
@RequiredArgsConstructor
@Tag(name = "Producción - Insumos", description = "Catálogo de materias primas y materiales de empaque")
public class InsumoController {

    private final InsumoService insumoService;

    @GetMapping
    @Operation(summary = "Listar catálogo de insumos")
    public ResponseEntity<List<InsumoDto>> listarTodos() {
        return ResponseEntity.ok(insumoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un insumo por ID")
    public ResponseEntity<InsumoDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(insumoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo insumo o materia prima")
    public ResponseEntity<InsumoDto> crear(@Valid @RequestBody InsumoCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(insumoService.crear(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de insumo")
    public ResponseEntity<InsumoDto> actualizar(@PathVariable Long id, @Valid @RequestBody InsumoCreateRequest req) {
        return ResponseEntity.ok(insumoService.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar insumo")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        insumoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
