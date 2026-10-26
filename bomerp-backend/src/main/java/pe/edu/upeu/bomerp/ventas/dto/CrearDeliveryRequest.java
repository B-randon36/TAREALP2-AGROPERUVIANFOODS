package pe.edu.upeu.bomerp.ventas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearDeliveryRequest {

    private String codigoPedido;

    private Long ventaId;

    private Long clienteId;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 150)
    private String clienteNombre;

    @Size(max = 30)
    private String clienteTelefono;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 250)
    private String direccionEntrega;

    @Size(max = 250)
    private String referencia;

    @NotNull(message = "El costo de envío es obligatorio")
    @DecimalMin(value = "0.00", message = "El costo de envío no puede ser negativo")
    private BigDecimal costoEnvio;

    private BigDecimal montoTotal;

    @Size(max = 120)
    private String repartidorNombre;

    private LocalDateTime fechaPactada;

    @Size(max = 500)
    private String observaciones;
}
