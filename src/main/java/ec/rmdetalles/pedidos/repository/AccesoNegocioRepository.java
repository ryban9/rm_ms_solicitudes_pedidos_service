package ec.rmdetalles.pedidos.repository;

import ec.rmdetalles.pedidos.domain.Negocio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface AccesoNegocioRepository extends Repository<Negocio, UUID> {
    @Query(value = """
        SELECT r.codigo AS "rolCodigo", p.codigo AS "permisoCodigo"
        FROM rm_detalles.usuario_negocios un
        JOIN rm_detalles.usuarios u ON u.id = un.usuario_id AND u.activo = TRUE
        JOIN rm_detalles.negocios n ON n.id = un.negocio_id AND n.activo = TRUE
        JOIN rm_detalles.roles r ON r.id = un.rol_id AND r.activo = TRUE
        LEFT JOIN rm_detalles.rol_permisos rp ON rp.rol_id = r.id AND rp.concedido = TRUE
        LEFT JOIN rm_detalles.permisos p ON p.id = rp.permiso_id
        WHERE un.usuario_id = :usuarioId AND n.codigo = :codigo AND un.activo = TRUE
        """, nativeQuery = true)
    List<AccesoNegocioProjection> buscarAcceso(@Param("usuarioId") UUID usuarioId, @Param("codigo") String codigo);
}
