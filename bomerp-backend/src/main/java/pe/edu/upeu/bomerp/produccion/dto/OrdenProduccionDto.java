package pe.edu.upeu.bomerp.produccion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.bomerp.produccion.entity.EstadoOrdenProduccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenProduccionDto {
    private Long id;
    private String codigoOrden;
    private Long productoId;
    private String productoNombre;
    private Long recetaBomId;
    private String recetaNombre;
    private BigDecimal cantidadAProducir;
    private Long loteGeneradoId;
    private String codigoLoteGenerado;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaFinalizacion;
    private EstadoOrdenProduccion estado;
    private String observaciones;
    private List<DetalleConsumoDto> consumos;
}
