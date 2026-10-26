package pe.edu.upeu.bomerp.produccion.service;

import pe.edu.upeu.bomerp.produccion.dto.InsumoCreateRequest;
import pe.edu.upeu.bomerp.produccion.dto.InsumoDto;
import pe.edu.upeu.bomerp.produccion.entity.Insumo;

import java.math.BigDecimal;
import java.util.List;

public interface InsumoService {
    List<InsumoDto> listarTodos();
    InsumoDto obtenerPorId(Long id);
    Insumo obtenerEntidadPorId(Long id);
    InsumoDto crear(InsumoCreateRequest req);
    InsumoDto actualizar(Long id, InsumoCreateRequest req);
    void eliminar(Long id);
    void actualizarStock(Long id, BigDecimal deltaCantidad, BigDecimal nuevoCostoPonderado);
}
