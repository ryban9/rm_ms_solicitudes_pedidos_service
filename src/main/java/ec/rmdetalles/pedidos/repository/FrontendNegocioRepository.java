package ec.rmdetalles.pedidos.repository;

import ec.rmdetalles.pedidos.domain.Negocio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface FrontendNegocioRepository extends Repository<Negocio, UUID> {
    @Query(value = "SELECT EXISTS (SELECT 1 FROM rm_detalles.frontends_negocio f JOIN rm_detalles.negocios n ON n.id=f.negocio_id WHERE n.codigo=:codigo AND f.origen=:origen AND f.activo=TRUE AND n.activo=TRUE)", nativeQuery = true)
    boolean existeActivoPorNegocioYOrigen(@Param("codigo") String codigo, @Param("origen") String origen);
    @Query(value = "SELECT EXISTS (SELECT 1 FROM rm_detalles.frontends_negocio f WHERE f.origen=:origen AND f.activo=TRUE)", nativeQuery = true)
    boolean existeOrigenActivo(@Param("origen") String origen);
}
