package pe.edu.upeu.bomerp.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.bomerp.ventas.entity.EstadoDelivery;
import pe.edu.upeu.bomerp.ventas.entity.PedidoDelivery;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoDeliveryRepository extends JpaRepository<PedidoDelivery, Long> {
    Optional<PedidoDelivery> findByCodigoPedido(String codigoPedido);
    List<PedidoDelivery> findByEstadoDeliveryOrderByFechaPactadaAsc(EstadoDelivery estado);
    List<PedidoDelivery> findAllByOrderByFechaPactadaDesc();
}
