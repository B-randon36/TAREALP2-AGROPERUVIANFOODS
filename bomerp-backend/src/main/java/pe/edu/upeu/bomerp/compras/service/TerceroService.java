package pe.edu.upeu.bomerp.compras.service;

import pe.edu.upeu.bomerp.compras.dto.TerceroCreateRequest;
import pe.edu.upeu.bomerp.compras.dto.TerceroDto;
import pe.edu.upeu.bomerp.compras.entity.Tercero;
import pe.edu.upeu.bomerp.compras.entity.TipoTercero;

import java.util.List;

public interface TerceroService {
    List<TerceroDto> listarTodos();
    List<TerceroDto> listarPorTipo(TipoTercero tipo);
    TerceroDto obtenerPorId(Long id);
    Tercero obtenerEntidadPorId(Long id);
    TerceroDto crear(TerceroCreateRequest req);
    TerceroDto actualizar(Long id, TerceroCreateRequest req);
    void eliminar(Long id);
}
