package pe.edu.upeu.bomerp.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCompraRequest {

    @NotNull(message = "El ID de insumo es obligatorio")
    private Long insumoId;

    @NotNull(message = "La cantidad comprada es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
    private BigDecimal cantidad;

    @NotNull(message = "El precio unitario de compra es obligatorio")
    @DecimalMin(value = "0.0001", message = "El precio unitario debe ser mayor a 0")
    private BigDecimal precioUnitario;
}
