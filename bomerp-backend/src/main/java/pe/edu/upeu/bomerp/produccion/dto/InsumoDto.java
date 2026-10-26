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
public class InsumoDto {
    private Long id;
    private String codigo;
    private String nombre;
    private UnidadMedida unidadMedida;
    private BigDecimal costoUnitario;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private Boolean activo;
}
