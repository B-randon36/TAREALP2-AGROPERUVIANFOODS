package pe.edu.upeu.bomerp.catalogo.producto.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ProductoResumen {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;

    public ProductoResumen() {
    }

    public ProductoResumen(Long id, String nombre, BigDecimal precio, Integer stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }
}
