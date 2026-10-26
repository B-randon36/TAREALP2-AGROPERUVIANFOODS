package pe.edu.upeu.bomerp.compras.entity;

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

@Entity
@Table(name = "TBL_TERCEROS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tercero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TIPO_DOCUMENTO", nullable = false, length = 20)
    private String tipoDocumento;

    @Column(name = "NUMERO_DOCUMENTO", nullable = false, unique = true, length = 30)
    private String numeroDocumento;

    @Column(name = "RAZON_SOCIAL", nullable = false, length = 150)
    private String razonSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_TERCERO", nullable = false, length = 20)
    private TipoTercero tipoTercero;

    @Column(name = "TELEFONO", length = 30)
    private String telefono;

    @Column(name = "DIRECCION", length = 250)
    private String direccion;

    @Column(name = "CORREO", length = 100)
    private String correo;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
