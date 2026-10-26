package pe.edu.upeu.bomerp.produccion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.bomerp.produccion.entity.RecetaBOM;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaBomRepository extends JpaRepository<RecetaBOM, Long> {
    Optional<RecetaBOM> findByCodigoReceta(String codigoReceta);
    List<RecetaBOM> findByProductoId(Long productoId);
    List<RecetaBOM> findByVigenteTrue();
}
