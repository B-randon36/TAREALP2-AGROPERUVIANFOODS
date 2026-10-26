package pe.edu.upeu.bomerp.produccion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearOrdenRequest {

    @NotBlank(message = "El código de orden es obligatorio")
    @Size(max = 40)
    private String codigoOrden;

    @NotNull(message = "El ID de receta BOM es obligatorio")
    private Long recetaBomId;

    @NotNull(message = "La cantidad a producir es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad a producir debe ser mayor a 0")
    private BigDecimal cantidadAProducir;

    @Size(max = 500)
    private String observaciones;
}
