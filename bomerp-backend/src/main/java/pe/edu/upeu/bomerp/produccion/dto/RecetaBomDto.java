package pe.edu.upeu.bomerp.produccion.dto;

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
public class RecetaBomDto {
    private Long id;
    private String codigoReceta;
    private Long productoId;
    private String productoNombre;
    private String nombreVersion;
    private BigDecimal costoManoObraEstimado;
    private BigDecimal costoTotalProduccion;
    private Boolean vigente;
    private List<DetalleRecetaDto> detalles;
}
