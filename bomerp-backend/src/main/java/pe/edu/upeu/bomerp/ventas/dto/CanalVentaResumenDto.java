package pe.edu.upeu.bomerp.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.bomerp.ventas.entity.CanalVenta;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanalVentaResumenDto {
    private CanalVenta canal;
    private String canalNombre;
    private BigDecimal totalVentas;
    private Long cantidadTransacciones;
    private BigDecimal ticketPromedio;
    private BigDecimal porcentaje;
}
