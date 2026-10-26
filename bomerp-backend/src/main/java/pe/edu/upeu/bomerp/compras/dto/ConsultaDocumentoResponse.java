package pe.edu.upeu.bomerp.compras.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDocumentoResponse {
    private String tipoDocumento;
    private String numeroDocumento;
    private String razonSocial;
    private String estado;
    private String condicion;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private boolean exito;
    private String mensaje;
}
