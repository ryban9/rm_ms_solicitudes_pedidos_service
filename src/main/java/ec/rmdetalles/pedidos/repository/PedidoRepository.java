package ec.rmdetalles.pedidos.repository;

import ec.rmdetalles.pedidos.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByNegocio_IdAndEstadoCodigoOrderByCreadoEnDesc(UUID negocioId, String estadoCodigo);
    List<Pedido> findByNegocio_IdAndEstadoCodigoNotOrderByCreadoEnDesc(UUID negocioId, String estadoCodigo);
    Optional<Pedido> findByIdAndNegocio_Id(UUID id, UUID negocioId);
    Optional<Pedido> findByIdAndNegocio_IdAndEstadoCodigo(UUID id, UUID negocioId, String estadoCodigo);
}
