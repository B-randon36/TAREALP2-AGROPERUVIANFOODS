package pe.edu.upeu.bomerp.produccion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.bomerp.produccion.entity.EstadoOrdenProduccion;
import pe.edu.upeu.bomerp.produccion.entity.OrdenProduccion;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenProduccionRepository extends JpaRepository<OrdenProduccion, Long> {
    Optional<OrdenProduccion> findByCodigoOrden(String codigoOrden);
    List<OrdenProduccion> findByEstado(EstadoOrdenProduccion estado);
    List<OrdenProduccion> findAllByOrderByFechaRegistroDesc();
}
