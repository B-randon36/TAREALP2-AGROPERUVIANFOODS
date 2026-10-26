package pe.edu.upeu.bomerp.produccion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.bomerp.produccion.entity.UnidadMedida;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleConsumoDto {
    private Long id;
    private Long insumoId;
    private String insumoCodigo;
    private String insumoNombre;
    private UnidadMedida unidadMedida;
    private BigDecimal cantidadConsumida;
    private BigDecimal costoUnitario;
}
