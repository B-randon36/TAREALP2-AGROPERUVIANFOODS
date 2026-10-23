package pe.edu.upeu.bomerp.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.bomerp.ventas.entity.Venta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    Optional<Venta> findByCodigoVenta(String codigoVenta);

    List<Venta> findAllByOrderByFechaHoraDesc();

    List<Venta> findBySesionCajaIdOrderByFechaHoraDesc(Long sesionCajaId);

    @Query("""
        SELECT v FROM Venta v
        WHERE v.fechaHora BETWEEN :inicio AND :fin
        ORDER BY v.fechaHora DESC
    """)
    List<Venta> findByRangoFechas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    long countBySesionCajaId(Long sesionCajaId);
}
