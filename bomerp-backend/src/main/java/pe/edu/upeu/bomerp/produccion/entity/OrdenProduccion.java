package pe.edu.upeu.bomerp.produccion.entity;

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
@Table(name = "TBL_ORDENES_PRODUCCION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODIGO_ORDEN", nullable = false, unique = true, length = 40)
    private String codigoOrden;

    @Column(name = "PRODUCTO_ID", nullable = false)
    private Long productoId;

    @Column(name = "PRODUCTO_NOMBRE", length = 150)
    private String productoNombre;

    @Column(name = "RECETA_BOM_ID", nullable = false)
    private Long recetaBomId;

    @Column(name = "CANTIDAD_A_PRODUCIR", nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidadAProducir;

    @Column(name = "LOTE_GENERADO_ID")
    private Long loteGeneradoId;

    @Column(name = "CODIGO_LOTE_GENERADO", length = 50)
    private String codigoLoteGenerado;

    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "FECHA_FINALIZACION")
    private LocalDateTime fechaFinalizacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 30)
    private EstadoOrdenProduccion estado;

    @Column(name = "OBSERVACIONES", length = 500)
    private String observaciones;

    @Builder.Default
    @OneToMany(mappedBy = "ordenProduccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleConsumoInsumo> consumos = new ArrayList<>();

    public void addConsumo(DetalleConsumoInsumo consumo) {
        consumos.add(consumo);
        consumo.setOrdenProduccion(this);
    }
}
