package pe.edu.upeu.bomerp.compras.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoCambioResponse {
    private String origen;
    private String moneda;
    private String fecha;
    private BigDecimal compra;
    private BigDecimal venta;
}
