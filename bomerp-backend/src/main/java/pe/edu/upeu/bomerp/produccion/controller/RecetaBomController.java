package pe.edu.upeu.bomerp.produccion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.produccion.dto.RecetaBomCreateRequest;
import pe.edu.upeu.bomerp.produccion.dto.RecetaBomDto;
import pe.edu.upeu.bomerp.produccion.service.RecetaBomService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/produccion/recetas")
@RequiredArgsConstructor
@Tag(name = "Producción - Recetas BOM", description = "Fichas técnicas y lista de materiales de fabricación")
public class RecetaBomController {

    private final RecetaBomService recetaBomService;

    @GetMapping
    @Operation(summary = "Listar todas las recetas técnicas BOM")
    public ResponseEntity<List<RecetaBomDto>> listarTodas() {
        return ResponseEntity.ok(recetaBomService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle completo de receta con insumos")
    public ResponseEntity<RecetaBomDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recetaBomService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva receta BOM con costeo automático de insumos")
    public ResponseEntity<RecetaBomDto> crear(@Valid @RequestBody RecetaBomCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaBomService.crear(req));
    }

    @PatchMapping("/{id}/vigencia")
    @Operation(summary = "Habilitar o deshabilitar vigencia de receta")
    public ResponseEntity<Void> cambiarVigencia(@PathVariable Long id, @RequestParam boolean vigente) {
        recetaBomService.cambiarVigencia(id, vigente);
        return ResponseEntity.noContent().build();
    }
}
