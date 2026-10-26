package pe.edu.upeu.bomerp.produccion.entity;

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

@Entity
@Table(name = "TBL_INSUMOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "UNIDAD_MEDIDA", nullable = false, length = 20)
    private UnidadMedida unidadMedida;

    @Column(name = "COSTO_UNITARIO", nullable = false, precision = 12, scale = 4)
    private BigDecimal costoUnitario;

    @Column(name = "STOCK_ACTUAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal stockActual;

    @Column(name = "STOCK_MINIMO", nullable = false, precision = 12, scale = 2)
    private BigDecimal stockMinimo;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
