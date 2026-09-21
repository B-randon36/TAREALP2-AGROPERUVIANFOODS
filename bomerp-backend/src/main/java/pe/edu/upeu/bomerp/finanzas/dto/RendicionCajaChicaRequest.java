package pe.edu.upeu.bomerp.finanzas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RendicionCajaChicaRequest {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El responsable es obligatorio")
    private String responsable;

    @NotNull(message = "El ID de la caja chica es obligatorio")
    private Long cajaChicaId;

    @NotEmpty(message = "La rendición debe contener al menos un detalle")
    @Valid
    private List<DetalleRendicionRequest> detalles;
}
