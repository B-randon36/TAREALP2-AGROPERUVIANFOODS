package pe.edu.upeu.bomerp.finanzas.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.bomerp.finanzas.dto.AperturaSesionRequest;
import pe.edu.upeu.bomerp.finanzas.dto.CierreCajaReporteDto;
import pe.edu.upeu.bomerp.finanzas.dto.CierreSesionRequest;
import pe.edu.upeu.bomerp.finanzas.dto.MovimientoCajaRequest;
import pe.edu.upeu.bomerp.finanzas.dto.MovimientoCajaResponse;
import pe.edu.upeu.bomerp.finanzas.dto.SesionCajaDetalleResponse;
import pe.edu.upeu.bomerp.finanzas.dto.SesionCajaResponse;
import pe.edu.upeu.bomerp.finanzas.service.SesionCajaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finanzas/sesiones")
public class SesionCajaController {

    private final SesionCajaService sesionCajaService;

    public SesionCajaController(SesionCajaService sesionCajaService) {
        this.sesionCajaService = sesionCajaService;
    }

    @PostMapping("/abrir")
    public ResponseEntity<SesionCajaResponse> abrirSesion(@Valid @RequestBody AperturaSesionRequest request) {
        SesionCajaResponse response = sesionCajaService.abrirSesion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/cerrar")
    public ResponseEntity<SesionCajaResponse> cerrarSesion(
            @PathVariable Long id,
            @Valid @RequestBody CierreSesionRequest request) {
        SesionCajaResponse response = sesionCajaService.cerrarSesion(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/movimientos")
    public ResponseEntity<MovimientoCajaResponse> registrarMovimiento(
            @PathVariable Long id,
            @Valid @RequestBody MovimientoCajaRequest request) {
        MovimientoCajaResponse response = sesionCajaService.registrarMovimiento(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SesionCajaResponse>> listarSesiones() {
        return ResponseEntity.ok(sesionCajaService.listarSesiones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SesionCajaDetalleResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(sesionCajaService.obtenerDetalle(id));
    }

    @GetMapping("/activa")
    public ResponseEntity<SesionCajaResponse> obtenerUltimaSesionActiva() {
        return ResponseEntity.ok(sesionCajaService.obtenerUltimaSesionActiva());
    }

    @GetMapping("/activa/usuario/{usuarioId}")
    public ResponseEntity<SesionCajaResponse> obtenerSesionActivaUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(sesionCajaService.obtenerSesionActivaUsuario(usuarioId));
    }

    @GetMapping("/{id}/datos-cierre")
    public ResponseEntity<CierreCajaReporteDto> obtenerDatosCierre(@PathVariable Long id) {
        return ResponseEntity.ok(sesionCajaService.generarDatosReporte(id));
    }
}
