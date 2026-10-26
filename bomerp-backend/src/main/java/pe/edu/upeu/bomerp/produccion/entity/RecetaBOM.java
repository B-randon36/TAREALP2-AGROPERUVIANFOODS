package pe.edu.upeu.bomerp.produccion.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBL_RECETAS_BOM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaBOM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODIGO_RECETA", nullable = false, unique = true, length = 40)
    private String codigoReceta;

    @Column(name = "PRODUCTO_ID", nullable = false)
    private Long productoId;

    @Column(name = "PRODUCTO_NOMBRE", length = 150)
    private String productoNombre;

    @Column(name = "NOMBRE_VERSION", nullable = false, length = 100)
    private String nombreVersion;

    @Column(name = "COSTO_MANO_OBRA_ESTIMADO", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoManoObraEstimado;

    @Column(name = "COSTO_TOTAL_PRODUCCION", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoTotalProduccion;

    @Column(name = "VIGENTE", nullable = false)
    private Boolean vigente;

    @Builder.Default
    @OneToMany(mappedBy = "recetaBom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRecetaInsumo> detalles = new ArrayList<>();

    public void addDetalle(DetalleRecetaInsumo detalle) {
        detalles.add(detalle);
        detalle.setRecetaBom(this);
    }
}
