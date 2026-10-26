package pe.edu.upeu.bomerp.compras.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.bomerp.compras.entity.Compra;
import pe.edu.upeu.bomerp.compras.entity.EstadoCompra;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    Optional<Compra> findByCodigoCompra(String codigoCompra);
    List<Compra> findByProveedorId(Long proveedorId);
    List<Compra> findByEstado(EstadoCompra estado);
    List<Compra> findAllByOrderByFechaCompraDesc();
}
