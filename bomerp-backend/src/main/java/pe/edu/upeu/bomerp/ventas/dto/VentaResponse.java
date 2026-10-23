package pe.edu.upeu.bomerp.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.bomerp.ventas.entity.CanalVenta;
import pe.edu.upeu.bomerp.ventas.entity.EstadoVenta;
import pe.edu.upeu.bomerp.ventas.entity.MetodoPagoVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponse {
    private Long id;
    private String codigoVenta;
    private LocalDateTime fechaHora;
    private CanalVenta canal;
    private String clienteTipo;
    private String clienteNombre;
    private String clienteNumeroDoc;
    private MetodoPagoVenta metodoPago;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private BigDecimal montoPagado;
    private BigDecimal vuelto;
    private Long sesionCajaId;
    private String cajeroNombre;
    private EstadoVenta estado;
    private List<DetalleVentaResponse> detalles;
}
