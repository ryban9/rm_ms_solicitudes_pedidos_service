package ec.rmdetalles.pedidos.repository;

import ec.rmdetalles.pedidos.domain.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, UUID> {
    List<PedidoDetalle> findByNegocioIdAndPedidoIdOrderByIdAsc(UUID negocioId, UUID pedidoId);
    @Modifying
    @Query("delete from PedidoDetalle d where d.negocioId = :negocioId and d.pedidoId = :pedidoId")
    void eliminarPorPedido(@Param("negocioId") UUID negocioId, @Param("pedidoId") UUID pedidoId);
}
