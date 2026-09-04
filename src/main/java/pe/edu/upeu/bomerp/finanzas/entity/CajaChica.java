package pe.edu.upeu.bomerp.finanzas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "CAJAS_CHICAS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CajaChica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "RESPONSABLE", nullable = false, length = 100)
    private String responsable;

    @Column(name = "SALDO_DISPONIBLE", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoDisponible;

    @Column(name = "LIMITE_MAXIMO", nullable = false, precision = 12, scale = 2)
    private BigDecimal limiteMaximo;
}
