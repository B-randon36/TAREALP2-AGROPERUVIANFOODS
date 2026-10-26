package pe.edu.upeu.bomerp.produccion.entity;

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
@Table(name = "TBL_DETALLES_RECETA_INSUMO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleRecetaInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECETA_BOM_ID", nullable = false)
    private RecetaBOM recetaBom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "INSUMO_ID", nullable = false)
    private Insumo insumo;

    @Column(name = "CANTIDAD_REQUERIDA", nullable = false, precision = 12, scale = 4)
    private BigDecimal cantidadRequerida;

    @Column(name = "COSTO_SUBTOTAL", nullable = false, precision = 12, scale = 4)
    private BigDecimal costoSubtotal;
}
