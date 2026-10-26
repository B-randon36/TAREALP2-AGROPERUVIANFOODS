package pe.edu.upeu.bomerp.produccion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class RecetaBomCreateRequest {

    @NotBlank(message = "El código de receta es obligatorio")
    @Size(max = 40)
    private String codigoReceta;

    @NotNull(message = "El ID de producto terminado es obligatorio")
    private Long productoId;

    @NotBlank(message = "El nombre de versión es obligatorio")
    @Size(max = 100)
    private String nombreVersion;

    @NotNull(message = "El costo de mano de obra estimado es obligatorio")
    @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
    private BigDecimal costoManoObraEstimado;

    @NotEmpty(message = "La receta debe contener al menos un insumo")
    @Valid
    private List<ItemRecetaRequest> items;
}
