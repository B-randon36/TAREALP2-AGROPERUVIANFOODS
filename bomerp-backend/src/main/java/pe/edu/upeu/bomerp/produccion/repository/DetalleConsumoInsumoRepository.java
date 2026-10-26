package pe.edu.upeu.bomerp.produccion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.bomerp.produccion.entity.DetalleConsumoInsumo;

import java.util.List;

@Repository
public interface DetalleConsumoInsumoRepository extends JpaRepository<DetalleConsumoInsumo, Long> {
    List<DetalleConsumoInsumo> findByOrdenProduccionId(Long ordenProduccionId);
}
