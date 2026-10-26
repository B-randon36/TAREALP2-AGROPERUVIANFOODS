package pe.edu.upeu.bomerp.compras.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.bomerp.compras.entity.Tercero;
import pe.edu.upeu.bomerp.compras.entity.TipoTercero;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerceroRepository extends JpaRepository<Tercero, Long> {
    Optional<Tercero> findByNumeroDocumento(String numeroDocumento);
    List<Tercero> findByTipoTerceroInAndActivoTrue(List<TipoTercero> tipos);
    List<Tercero> findByActivoTrue();
}
