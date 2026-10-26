package pe.edu.upeu.bomerp.ventas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_PEDIDOS_DELIVERY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODIGO_PEDIDO", nullable = false, unique = true, length = 40)
    private String codigoPedido;

    @Column(name = "VENTA_ID")
    private Long ventaId;

    @Column(name = "CLIENTE_ID")
    private Long clienteId;

    @Column(name = "CLIENTE_NOMBRE", nullable = false, length = 150)
    private String clienteNombre;

    @Column(name = "CLIENTE_TELEFONO", length = 30)
    private String clienteTelefono;

    @Column(name = "DIRECCION_ENTREGA", nullable = false, length = 250)
    private String direccionEntrega;

    @Column(name = "REFERENCIA", length = 250)
    private String referencia;

    @Column(name = "COSTO_ENVIO", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    @Column(name = "MONTO_TOTAL", precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "REPARTIDOR_NOMBRE", length = 120)
    private String repartidorNombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_DELIVERY", nullable = false, length = 20)
    private EstadoDelivery estadoDelivery;

    @Column(name = "FECHA_PACTADA")
    private LocalDateTime fechaPactada;

    @Column(name = "FECHA_ENTREGA")
    private LocalDateTime fechaEntrega;

    @Column(name = "OBSERVACIONES", length = 500)
    private String observaciones;
}
