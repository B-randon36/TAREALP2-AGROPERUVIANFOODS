package pe.edu.upeu.bomerp.compras.service;

import pe.edu.upeu.bomerp.compras.dto.CompraDto;
import pe.edu.upeu.bomerp.compras.dto.RegistrarCompraRequest;

import java.util.List;

public interface CompraService {
    List<CompraDto> listarTodas();
    CompraDto obtenerPorId(Long id);
    CompraDto registrarCompra(RegistrarCompraRequest req);
    CompraDto recibirCompra(Long id);
    CompraDto anularCompra(Long id, String motivo);
}
