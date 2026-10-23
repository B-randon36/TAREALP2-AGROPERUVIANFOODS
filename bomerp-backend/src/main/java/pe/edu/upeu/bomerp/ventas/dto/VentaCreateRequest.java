package pe.edu.upeu.bomerp.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.bomerp.ventas.entity.CanalVenta;
import pe.edu.upeu.bomerp.ventas.entity.MetodoPagoVenta;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaCreateRequest {

    @NotNull(message = "El canal de venta es obligatorio")
    private CanalVenta canal;

    private String clienteTipo;
    private String clienteNombre;
    private String clienteNumeroDoc;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPagoVenta metodoPago;

    private BigDecimal montoPagado;
    private String cajeroNombre;

    @NotEmpty(message = "Debe incluir al menos un producto en la venta")
    @Valid
    private List<VentaItemRequest> items;
}
