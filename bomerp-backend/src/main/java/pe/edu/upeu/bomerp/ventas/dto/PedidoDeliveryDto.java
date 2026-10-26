package pe.edu.upeu.bomerp.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.bomerp.ventas.entity.EstadoDelivery;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDeliveryDto {
    private Long id;
    private String codigoPedido;
    private Long ventaId;
    private Long clienteId;
    private String clienteNombre;
    private String clienteTelefono;
    private String direccionEntrega;
    private String referencia;
    private BigDecimal costoEnvio;
    private BigDecimal montoTotal;
    private String repartidorNombre;
    private EstadoDelivery estadoDelivery;
    private LocalDateTime fechaPactada;
    private LocalDateTime fechaEntrega;
    private String observaciones;
}
