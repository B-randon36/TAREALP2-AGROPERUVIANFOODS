package pe.edu.upeu.bomerp.finanzas.dto;

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
public class TransaccionFinancieraResponse {
    private Long id;
    private String codigo;
    private String concepto;
    private BigDecimal monto;
    private LocalDate fechaTransaccion;
    private TipoFinanciero tipo;
    private EstadoFinanciero estado;
    private String tipoComprobante;
    private String numeroComprobante;
    private CategoriaFinancieraResumen categoriaFinanciera;
}
