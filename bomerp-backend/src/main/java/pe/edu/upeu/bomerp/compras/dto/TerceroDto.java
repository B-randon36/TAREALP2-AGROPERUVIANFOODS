package pe.edu.upeu.bomerp.compras.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.bomerp.compras.entity.TipoTercero;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerceroDto {
    private Long id;
    private String tipoDocumento;
    private String numeroDocumento;
    private String razonSocial;
    private TipoTercero tipoTercero;
    private String telefono;
    private String direccion;
    private String correo;
    private Boolean activo;
}
