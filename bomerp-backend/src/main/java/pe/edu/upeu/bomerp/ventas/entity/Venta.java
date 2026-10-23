package pe.edu.upeu.bomerp.ventas.entity;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBL_VENTAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODIGO_VENTA", nullable = false, unique = true, length = 40)
    private String codigoVenta;

    @Column(name = "FECHA_HORA", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "CANAL", nullable = false, length = 30)
    private CanalVenta canal;

    @Column(name = "CLIENTE_TIPO", nullable = false, length = 30)
    private String clienteTipo;

    @Column(name = "CLIENTE_NOMBRE", nullable = false, length = 150)
    private String clienteNombre;

    @Column(name = "CLIENTE_NUMERO_DOC", length = 20)
    private String clienteNumeroDoc;

    @Enumerated(EnumType.STRING)
    @Column(name = "METODO_PAGO", nullable = false, length = 30)
    private MetodoPagoVenta metodoPago;

    @Column(name = "SUBTOTAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "IGV", nullable = false, precision = 12, scale = 2)
    private BigDecimal igv;

    @Column(name = "TOTAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "MONTO_PAGADO", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "VUELTO", nullable = false, precision = 12, scale = 2)
    private BigDecimal vuelto;

    @Column(name = "SESION_CAJA_ID")
    private Long sesionCajaId;

    @Column(name = "CAJERO_NOMBRE", length = 120)
    private String cajeroNombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoVenta estado;

    @Builder.Default
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public void addDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }
}
