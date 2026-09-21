package pe.edu.upeu.bomerp.finanzas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CajaChicaResponse {
    private Long id;
    private String codigo;
    private String responsable;
    private BigDecimal saldoDisponible;
    private BigDecimal limiteMaximo;
}
