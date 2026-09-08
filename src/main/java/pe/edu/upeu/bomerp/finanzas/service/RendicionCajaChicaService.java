package pe.edu.upeu.bomerp.finanzas.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.bomerp.finanzas.dto.CajaChicaResponse;
import pe.edu.upeu.bomerp.finanzas.dto.RendicionCajaChicaRequest;
import pe.edu.upeu.bomerp.finanzas.dto.RendicionCajaChicaResponse;

import java.math.BigDecimal;
import java.util.List;

public interface RendicionCajaChicaService {
    CajaChicaResponse crearCajaChica(String codigo, String responsable, BigDecimal limiteMaximo, BigDecimal saldoInicial);
    CajaChicaResponse obtenerCajaChica(Long id);

    RendicionCajaChicaResponse registrarRendicion(RendicionCajaChicaRequest request);
    RendicionCajaChicaResponse obtenerRendicion(Long id);
    List<RendicionCajaChicaResponse> listarRendicionesPorCaja(Long cajaChicaId);
    Page<RendicionCajaChicaResponse> listarPaginado(Pageable pageable);
}
