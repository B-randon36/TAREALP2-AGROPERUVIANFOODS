package pe.edu.upeu.bomerp.ventas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.bomerp.ventas.dto.VentaCreateRequest;
import pe.edu.upeu.bomerp.ventas.dto.VentaResponse;
import pe.edu.upeu.bomerp.ventas.service.VentaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas y Punto de Venta (POS)", description = "Operaciones de venta en mostrador conectadas con turnos de caja y descuento FEFO de lotes")
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    @Operation(summary = "Registrar nueva venta en mostrador sincronizada con turno de caja y descuento FEFO")
    public ResponseEntity<VentaResponse> registrarVenta(@Valid @RequestBody VentaCreateRequest request) {
        VentaResponse respuesta = ventaService.registrarVenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    @Operation(summary = "Listar ventas registradas")
    public ResponseEntity<List<VentaResponse>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle completo de venta por ID")
    public ResponseEntity<VentaResponse> obtenerVentaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerVentaPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener detalle completo de venta por código correlativo")
    public ResponseEntity<VentaResponse> obtenerVentaPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(ventaService.obtenerVentaPorCodigo(codigo));
    }

    @GetMapping("/reporte-omnicanal")
    @Operation(summary = "Reporte analítico de ventas por canal omnicanal y ranking Top 5 productos (Matriz ABC)")
    public ResponseEntity<pe.edu.upeu.bomerp.ventas.dto.ReporteOmnicanalResponse> obtenerReporteOmnicanal() {
        return ResponseEntity.ok(ventaService.obtenerReporteOmnicanal());
    }
}
