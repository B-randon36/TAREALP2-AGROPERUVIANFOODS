package pe.edu.upeu.bomerp.service;

import pe.edu.upeu.bomerp.dto.request.ProductoRequest;
import pe.edu.upeu.bomerp.dto.response.ProductoResponse;
import java.util.List;

public interface ProductoService {
    ProductoResponse crear(ProductoRequest request);
    List<ProductoResponse> listarTodos();
    ProductoResponse obtenerPorId(Long id);
    ProductoResponse actualizar(Long id, ProductoRequest request);
    void eliminar(Long id);
}
