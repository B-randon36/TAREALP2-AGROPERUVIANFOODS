package pe.edu.upeu.bomerp.produccion.service;

import pe.edu.upeu.bomerp.produccion.dto.CompletarOrdenRequest;
import pe.edu.upeu.bomerp.produccion.dto.CrearOrdenRequest;
import pe.edu.upeu.bomerp.produccion.dto.OrdenProduccionDto;

import java.util.List;

public interface OrdenProduccionService {
    List<OrdenProduccionDto> listarTodas();
    OrdenProduccionDto obtenerPorId(Long id);
    OrdenProduccionDto crearOrden(CrearOrdenRequest req);
    OrdenProduccionDto completarOrden(Long id, CompletarOrdenRequest req);
    OrdenProduccionDto cancelarOrden(Long id, String motivo);
}
