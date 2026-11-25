package ec.rmdetalles.pedidos.repository;

import ec.rmdetalles.pedidos.domain.TransicionPedido;
import ec.rmdetalles.pedidos.domain.TransicionPedidoId;
import org.springframework.data.repository.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransicionPedidoRepository extends Repository<TransicionPedido, TransicionPedidoId> {
    List<TransicionPedido> findByNegocioIdAndEstadoOrigenAndActivoTrue(UUID negocioId, String estadoOrigen);
    Optional<TransicionPedido> findByNegocioIdAndEstadoOrigenAndEstadoDestinoAndActivoTrue(UUID negocioId, String origen, String destino);
}
