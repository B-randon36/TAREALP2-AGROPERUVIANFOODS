package pe.edu.upeu.bomerp.finanzas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.bomerp.finanzas.entity.EstadoFinanciero;
import pe.edu.upeu.bomerp.finanzas.entity.TipoFinanciero;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionFinancieraRequest {
    @NotBlank(message = "El código de transacción es obligatorio")
    private String codigo;

    @NotBlank(message = "El concepto de la transacción es obligatorio")
    private String concepto;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    private LocalDate fechaTransaccion;

    @NotNull(message = "El tipo financiero (INGRESO/EGRESO) es obligatorio")
    private TipoFinanciero tipo;

    private EstadoFinanciero estado;

    private String tipoComprobante;

    private String numeroComprobante;

    @NotNull(message = "El ID de la categoría financiera es obligatorio")
    private Long categoriaFinancieraId;
}
