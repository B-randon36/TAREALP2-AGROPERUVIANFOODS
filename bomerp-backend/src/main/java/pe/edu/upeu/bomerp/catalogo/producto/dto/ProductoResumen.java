package pe.edu.upeu.bomerp.catalogo.producto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResumen {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
}
