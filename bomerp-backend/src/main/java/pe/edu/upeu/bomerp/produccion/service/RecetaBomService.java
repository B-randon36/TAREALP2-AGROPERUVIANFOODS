package pe.edu.upeu.bomerp.produccion.service;

import pe.edu.upeu.bomerp.produccion.dto.RecetaBomCreateRequest;
import pe.edu.upeu.bomerp.produccion.dto.RecetaBomDto;
import pe.edu.upeu.bomerp.produccion.entity.RecetaBOM;

import java.util.List;

public interface RecetaBomService {
    List<RecetaBomDto> listarTodas();
    RecetaBomDto obtenerPorId(Long id);
    RecetaBOM obtenerEntidadPorId(Long id);
    RecetaBomDto crear(RecetaBomCreateRequest req);
    void cambiarVigencia(Long id, boolean vigente);
}
