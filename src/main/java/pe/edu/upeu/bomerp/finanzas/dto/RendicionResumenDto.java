package pe.edu.upeu.bomerp.finanzas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.bomerp.finanzas.entity.EstadoRendicion;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de proyección optimizado para listar resúmenes de rendiciones.
 * Utilizado con expresiones de constructor en consultas JPQL (SELECT new ...).
 * Evita sobrecarga de memoria al no cargar la entidad completa ni colecciones pesadas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendicionResumenDto {

    private Long id;
    private String codigo;
    private String responsable;
    private LocalDate fecha;
    private BigDecimal montoTotal;
    private EstadoRendicion estado;
    private String codigoCajaChica;
    private Integer cantidadDetalles;
}
