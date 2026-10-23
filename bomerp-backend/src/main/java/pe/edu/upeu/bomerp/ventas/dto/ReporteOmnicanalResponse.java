package pe.edu.upeu.bomerp.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteOmnicanalResponse {
    private BigDecimal totalVentasOmnicanal;
    private Long totalTransacciones;
    private List<CanalVentaResumenDto> canales;
    private List<ProductoRentabilidadDto> rankingProductos;
}
