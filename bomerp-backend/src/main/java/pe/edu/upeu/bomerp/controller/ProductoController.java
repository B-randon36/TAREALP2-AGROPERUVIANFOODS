package pe.edu.upeu.bomerp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.dto.request.ProductoRequest;
import pe.edu.upeu.bomerp.dto.response.ProductoResponse;
import pe.edu.upeu.bomerp.exception.GlobalExceptionHandler.ErrorResponse;
import pe.edu.upeu.bomerp.service.ProductoService;

import java.util.List;

@Tag(name = "Productos", description = "API REST para la gestión de productos en Agro Peruvian Foods")
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Crear un nuevo producto", description = "Registra un producto en el sistema, validando que el código sea único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o código duplicado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.crear(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los productos", description = "Retorna una lista completa de los productos registrados.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente",
        content = @Content(schema = @Schema(implementation = ProductoResponse.class)))
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarTodos() {
        List<ProductoResponse> response = productoService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener producto por ID", description = "Retorna el producto correspondiente al ID suministrado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(
            @Parameter(description = "ID del producto a buscar", required = true)
            @PathVariable Long id) {
        ProductoResponse response = productoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar un producto existente", description = "Modifica los datos de un producto existente identificado por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o conflicto de código",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(
            @Parameter(description = "ID del producto a actualizar", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar un producto por ID", description = "Elimina físicamente el producto identificado por el ID suministrado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto a eliminar", required = true)
            @PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
