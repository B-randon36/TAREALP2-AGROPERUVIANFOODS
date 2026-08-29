package pe.edu.upeu.bomerp.finanzas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaFinancieraRequest {
    @NotBlank(message = "El nombre de la categoría financiera es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El tipo predeterminado (INGRESO/EGRESO) es obligatorio")
    private TipoFinanciero tipoDefault;
}
