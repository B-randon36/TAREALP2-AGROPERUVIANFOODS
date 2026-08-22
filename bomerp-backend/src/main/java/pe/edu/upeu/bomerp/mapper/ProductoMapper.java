package pe.edu.upeu.bomerp.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.bomerp.dto.request.ProductoRequest;
import pe.edu.upeu.bomerp.dto.response.ProductoResponse;
import pe.edu.upeu.bomerp.entity.Producto;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequest request) {
        if (request == null) {
            return null;
        }
        return Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .codigo(request.getCodigo())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();
    }

    public ProductoResponse toResponse(Producto entity) {
        if (entity == null) {
            return null;
        }
        return ProductoResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .codigo(entity.getCodigo())
                .precio(entity.getPrecio())
                .stock(entity.getStock())
                .activo(entity.getActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    public void updateEntityFromRequest(ProductoRequest request, Producto entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        entity.setCodigo(request.getCodigo());
        entity.setPrecio(request.getPrecio());
        entity.setStock(request.getStock());
        if (request.getActivo() != null) {
            entity.setActivo(request.getActivo());
        }
    }
}
