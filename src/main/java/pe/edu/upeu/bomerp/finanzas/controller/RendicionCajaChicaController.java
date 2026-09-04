package pe.edu.upeu.bomerp.finanzas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.bomerp.finanzas.dto.CajaChicaResponse;
import pe.edu.upeu.bomerp.finanzas.dto.RendicionCajaChicaRequest;
import pe.edu.upeu.bomerp.finanzas.dto.RendicionCajaChicaResponse;
import pe.edu.upeu.bomerp.finanzas.service.RendicionCajaChicaService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finanzas")
@RequiredArgsConstructor
public class RendicionCajaChicaController {

    private final RendicionCajaChicaService rendicionService;

    @PostMapping("/cajas-chicas")
    public ResponseEntity<CajaChicaResponse> crearCajaChica(
            @RequestParam String codigo,
            @RequestParam String responsable,
            @RequestParam BigDecimal limiteMaximo,
            @RequestParam BigDecimal saldoInicial) {
        CajaChicaResponse response = rendicionService.crearCajaChica(codigo, responsable, limiteMaximo, saldoInicial);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/cajas-chicas/{id}")
    public ResponseEntity<CajaChicaResponse> obtenerCajaChica(@PathVariable Long id) {
        return ResponseEntity.ok(rendicionService.obtenerCajaChica(id));
    }

    @PostMapping("/rendiciones")
    public ResponseEntity<RendicionCajaChicaResponse> registrarRendicion(@Valid @RequestBody RendicionCajaChicaRequest request) {
        RendicionCajaChicaResponse response = rendicionService.registrarRendicion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/rendiciones/{id}")
    public ResponseEntity<RendicionCajaChicaResponse> obtenerRendicion(@PathVariable Long id) {
        return ResponseEntity.ok(rendicionService.obtenerRendicion(id));
    }

    @GetMapping("/rendiciones/caja/{cajaChicaId}")
    public ResponseEntity<List<RendicionCajaChicaResponse>> listarPorCaja(@PathVariable Long cajaChicaId) {
        return ResponseEntity.ok(rendicionService.listarRendicionesPorCaja(cajaChicaId));
    }
}
