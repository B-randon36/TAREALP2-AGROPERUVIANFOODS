package pe.edu.upeu.bomerp.produccion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class InsumoCreateRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 40)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @NotNull(message = "La unidad de medida es obligatoria")
    private UnidadMedida unidadMedida;

    @NotNull(message = "El costo unitario es obligatorio")
    @DecimalMin(value = "0.0001", message = "El costo unitario debe ser mayor a 0")
    private BigDecimal costoUnitario;

    @NotNull(message = "El stock inicial es obligatorio")
    @DecimalMin(value = "0.00", message = "El stock no puede ser negativo")
    private BigDecimal stockActual;

    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.00", message = "El stock mínimo no puede ser negativo")
    private BigDecimal stockMinimo;
}
