package pe.edu.upeu.bomerp.produccion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRecetaRequest {

    @NotNull(message = "El ID de insumo es obligatorio")
    private Long insumoId;

    @NotNull(message = "La cantidad requerida es obligatoria")
    @DecimalMin(value = "0.0001", message = "La cantidad requerida debe ser mayor a 0")
    private BigDecimal cantidadRequerida;
}
