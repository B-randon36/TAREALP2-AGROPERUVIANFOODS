package pe.edu.upeu.bomerp.compras.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarCompraRequest {

    private String codigoCompra; // Opcional, autogenerado si viene vacío

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    @NotBlank(message = "El número de comprobante es obligatorio (factura o boleta)")
    @Size(max = 50)
    private String numeroComprobante;

    @NotNull(message = "La fecha de compra es obligatoria")
    private LocalDate fechaCompra;

    @NotBlank(message = "El método de pago es obligatorio (EFECTIVO, TRANSFERENCIA, CREDITO)")
    private String metodoPago;

    @Size(max = 500)
    private String observaciones;

    @NotEmpty(message = "La compra debe incluir al menos un insumo")
    @Valid
    private List<ItemCompraRequest> items;
}
