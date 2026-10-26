package pe.edu.upeu.bomerp.finanzas.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class DetalleRendicionResponse {
    private Long id;
    private String concepto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private Long productoId;

    public DetalleRendicionResponse() {
    }

    public DetalleRendicionResponse(Long id, String concepto, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal, Long productoId) {
        this.id = id;
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.productoId = productoId;
    }
}
