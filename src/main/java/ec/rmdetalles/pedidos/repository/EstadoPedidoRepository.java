package ec.rmdetalles.pedidos.repository;

import ec.rmdetalles.pedidos.domain.EstadoPedido;
import ec.rmdetalles.pedidos.domain.EstadoPedidoId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, EstadoPedidoId> {
    List<EstadoPedido> findByNegocioIdAndActivoTrueOrderByOrdenAsc(UUID negocioId);
    List<EstadoPedido> findByNegocioIdAndActivoTrueAndInicialTrue(UUID negocioId);
    Optional<EstadoPedido> findByNegocioIdAndCodigoAndActivoTrue(UUID negocioId, String codigo);
}
