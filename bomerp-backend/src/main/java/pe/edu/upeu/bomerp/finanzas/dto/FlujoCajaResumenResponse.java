package pe.edu.upeu.bomerp.finanzas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlujoCajaResumenResponse {
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal balanceNeto;
    private BigDecimal cuentasPorCobrarPendientes;
    private BigDecimal cuentasPorPagarPendientes;
    private long totalTransacciones;
}
