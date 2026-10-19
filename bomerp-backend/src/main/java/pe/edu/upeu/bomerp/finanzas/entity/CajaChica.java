package pe.edu.upeu.bomerp.finanzas.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pe.edu.upeu.bomerp.seguridad.usuario.entity.Usuario;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CAJAS_CHICAS")
@Getter
@Setter
@Builder
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

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "CAJA_CHICA_CUSTODIOS",
        joinColumns = @JoinColumn(name = "CAJA_CHICA_ID"),
        inverseJoinColumns = @JoinColumn(name = "USUARIO_ID")
    )
    private Set<Usuario> custodiosAutorizados = new HashSet<>();

    public CajaChica() {
    }

    public CajaChica(Long id, String codigo, String responsable, BigDecimal saldoDisponible, BigDecimal limiteMaximo) {
        this.id = id;
        this.codigo = codigo;
        this.responsable = responsable;
        this.saldoDisponible = saldoDisponible;
        this.limiteMaximo = limiteMaximo;
        this.custodiosAutorizados = new HashSet<>();
    }

    public CajaChica(Long id, String codigo, String responsable, BigDecimal saldoDisponible, BigDecimal limiteMaximo, Set<Usuario> custodiosAutorizados) {
        this.id = id;
        this.codigo = codigo;
        this.responsable = responsable;
        this.saldoDisponible = saldoDisponible;
        this.limiteMaximo = limiteMaximo;
        this.custodiosAutorizados = custodiosAutorizados != null ? custodiosAutorizados : new HashSet<>();
    }
}
