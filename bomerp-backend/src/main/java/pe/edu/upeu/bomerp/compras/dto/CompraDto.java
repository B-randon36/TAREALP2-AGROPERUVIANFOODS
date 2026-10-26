package pe.edu.upeu.bomerp.compras.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.bomerp.compras.entity.EstadoCompra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraDto {
    private Long id;
    private String codigoCompra;
    private Long proveedorId;
    private String proveedorNombre;
    private String numeroComprobante;
    private LocalDate fechaCompra;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private EstadoCompra estado;
    private String metodoPago;
    private String observaciones;
    private List<DetalleCompraDto> detalles;
}
