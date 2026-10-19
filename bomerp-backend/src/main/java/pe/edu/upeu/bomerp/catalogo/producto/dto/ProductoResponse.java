package pe.edu.upeu.bomerp.catalogo.producto.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaResumen;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ProductoResponse {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private CategoriaResumen categoria;

    public ProductoResponse() {
    }

    public ProductoResponse(Long id, String nombre, BigDecimal precio, Integer stock, CategoriaResumen categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }
}