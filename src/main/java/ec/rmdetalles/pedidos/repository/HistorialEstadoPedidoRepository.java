package ec.rmdetalles.pedidos.repository;

import ec.rmdetalles.pedidos.domain.HistorialEstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface HistorialEstadoPedidoRepository extends JpaRepository<HistorialEstadoPedido, UUID> {
    List<HistorialEstadoPedido> findByNegocioIdAndPedidoIdOrderByCreadoEnAsc(UUID negocioId, UUID pedidoId);
}
