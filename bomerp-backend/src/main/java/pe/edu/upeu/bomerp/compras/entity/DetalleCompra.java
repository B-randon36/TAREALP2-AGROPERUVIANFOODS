package pe.edu.upeu.bomerp.compras.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "TBL_DETALLES_COMPRA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPRA_ID", nullable = false)
    private Compra compra;

    @Column(name = "INSUMO_ID", nullable = false)
    private Long insumoId;

    @Column(name = "INSUMO_NOMBRE", length = 150)
    private String insumoNombre;

    @Column(name = "CANTIDAD", nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "PRECIO_UNITARIO", nullable = false, precision = 12, scale = 4)
    private BigDecimal precioUnitario;

    @Column(name = "SUBTOTAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
