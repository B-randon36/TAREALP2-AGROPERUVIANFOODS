package pe.edu.upeu.bomerp.compras.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.bomerp.compras.dto.ConsultaDocumentoResponse;
import pe.edu.upeu.bomerp.compras.dto.TipoCambioResponse;
import pe.edu.upeu.bomerp.compras.service.SunatApiService;

@RestController
@RequestMapping("/api/v1/sunat")
@RequiredArgsConstructor
@Tag(name = "Integraciones SUNAT & RENIEC", description = "Consultas oficiales de RUC, DNI y Tipo de Cambio SUNAT")
public class SunatApiController {

    private final SunatApiService sunatApiService;

    @GetMapping("/consulta-documento")
    @Operation(summary = "Consultar datos de RUC en SUNAT o DNI en RENIEC")
    public ResponseEntity<ConsultaDocumentoResponse> consultarDocumento(
            @RequestParam(required = false, defaultValue = "RUC") String tipo,
            @RequestParam String numero) {
        return ResponseEntity.ok(sunatApiService.consultarDocumento(tipo, numero));
    }

    @GetMapping("/tipo-cambio")
    @Operation(summary = "Consultar tipo de cambio oficial del día (USD a PEN) según SUNAT")
    public ResponseEntity<TipoCambioResponse> obtenerTipoCambio() {
        return ResponseEntity.ok(sunatApiService.obtenerTipoCambioActual());
    }
}
