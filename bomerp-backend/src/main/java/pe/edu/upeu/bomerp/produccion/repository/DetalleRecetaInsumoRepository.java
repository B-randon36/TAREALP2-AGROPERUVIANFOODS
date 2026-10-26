package pe.edu.upeu.bomerp.produccion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.bomerp.produccion.entity.DetalleRecetaInsumo;

import java.util.List;

@Repository
public interface DetalleRecetaInsumoRepository extends JpaRepository<DetalleRecetaInsumo, Long> {
    List<DetalleRecetaInsumo> findByRecetaBomId(Long recetaBomId);
}
