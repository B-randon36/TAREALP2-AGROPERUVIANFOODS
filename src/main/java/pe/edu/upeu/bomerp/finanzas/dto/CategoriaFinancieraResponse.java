package pe.edu.upeu.bomerp.finanzas.dto;

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
public class CategoriaFinancieraResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private TipoFinanciero tipoDefault;
}
