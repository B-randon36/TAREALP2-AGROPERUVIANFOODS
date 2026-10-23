package pe.edu.upeu.bomerp.ventas.service;

import pe.edu.upeu.bomerp.ventas.dto.VentaCreateRequest;
import pe.edu.upeu.bomerp.ventas.dto.VentaResponse;

import java.util.List;

public interface VentaService {

    VentaResponse registrarVenta(VentaCreateRequest request);

    List<VentaResponse> listarVentas();

    VentaResponse obtenerVentaPorId(Long id);

    VentaResponse obtenerVentaPorCodigo(String codigoVenta);

    pe.edu.upeu.bomerp.ventas.dto.ReporteOmnicanalResponse obtenerReporteOmnicanal();
}
