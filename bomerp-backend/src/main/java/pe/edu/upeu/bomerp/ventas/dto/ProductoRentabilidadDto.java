package pe.edu.upeu.bomerp.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRentabilidadDto {
    private Long productoId;
    private String productoNombre;
    private String categoria;
    private BigDecimal unidadesVendidas;
    private BigDecimal totalIngresos;
    private BigDecimal costoEstimado;
    private BigDecimal utilidadBruta;
    private BigDecimal margenContribucion; // Porcentaje de margen
    private String clasificacionAbc; // "A" (Top 80%), "B" (15%), "C" (5%)
}
