package pe.edu.upeu.bomerp.compras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.bomerp.compras.entity.TipoTercero;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerceroCreateRequest {

    @NotBlank(message = "El tipo de documento es obligatorio (DNI, RUC)")
    @Size(max = 20)
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 30)
    private String numeroDocumento;

    @NotBlank(message = "La razón social o nombre completo es obligatoria")
    @Size(max = 150)
    private String razonSocial;

    @NotNull(message = "El tipo de tercero es obligatorio")
    private TipoTercero tipoTercero;

    @Size(max = 30)
    private String telefono;

    @Size(max = 250)
    private String direccion;

    @Size(max = 100)
    private String correo;
}
