package ec.rmdetalles.pedidos.repository;

import ec.rmdetalles.pedidos.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    Optional<Producto> findByIdAndNegocioIdAndActivoTrue(UUID id, UUID negocioId);
}
