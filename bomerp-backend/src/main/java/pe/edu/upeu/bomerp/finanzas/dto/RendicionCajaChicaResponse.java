package pe.edu.upeu.bomerp.finanzas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.bomerp.finanzas.entity.EstadoRendicion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RendicionCajaChicaResponse {
    private Long id;
    private String codigo;
    private String responsable;
    private LocalDate fecha;
    private BigDecimal montoTotal;
    private EstadoRendicion estado;
    private Long cajaChicaId;
    private List<DetalleRendicionResponse> detalles;
}
