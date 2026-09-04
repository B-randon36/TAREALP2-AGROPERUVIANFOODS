package pe.edu.upeu.bomerp.finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.bomerp.finanzas.entity.RendicionCajaChica;

import java.util.List;
import java.util.Optional;

@Repository
public interface RendicionCajaChicaRepository extends JpaRepository<RendicionCajaChica, Long> {
    Optional<RendicionCajaChica> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<RendicionCajaChica> findByCajaChicaId(Long cajaChicaId);
}
