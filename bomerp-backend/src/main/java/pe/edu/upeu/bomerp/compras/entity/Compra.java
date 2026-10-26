package pe.edu.upeu.bomerp.compras.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBL_COMPRAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODIGO_COMPRA", nullable = false, unique = true, length = 40)
    private String codigoCompra;

    @Column(name = "PROVEEDOR_ID", nullable = false)
    private Long proveedorId;

    @Column(name = "PROVEEDOR_NOMBRE", length = 150)
    private String proveedorNombre;

    @Column(name = "NUMERO_COMPROBANTE", nullable = false, length = 50)
    private String numeroComprobante;

    @Column(name = "FECHA_COMPRA", nullable = false)
    private LocalDate fechaCompra;

    @Column(name = "SUBTOTAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "IGV", nullable = false, precision = 12, scale = 2)
    private BigDecimal igv;

    @Column(name = "TOTAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoCompra estado;

    @Column(name = "METODO_PAGO", nullable = false, length = 30)
    private String metodoPago;

    @Column(name = "OBSERVACIONES", length = 500)
    private String observaciones;

    @Builder.Default
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles = new ArrayList<>();

    public void addDetalle(DetalleCompra detalle) {
        detalles.add(detalle);
        detalle.setCompra(this);
    }
}
