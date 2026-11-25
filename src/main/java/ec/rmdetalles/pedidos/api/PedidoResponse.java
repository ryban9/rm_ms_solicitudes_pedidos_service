package ec.rmdetalles.pedidos.api;

import ec.rmdetalles.pedidos.domain.EstadoPedido;
import ec.rmdetalles.pedidos.domain.Pedido;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
        UUID id, UUID clienteId, String clienteNombre, String estadoCodigo, String estadoNombre,
        LocalDate fechaSolicitada, String detallePersonalizacion, String observaciones,
        OffsetDateTime creadoEn, OffsetDateTime actualizadoEn,
        List<PedidoDetalleResponse> detalles, List<HistorialEstadoResponse> historial
) {
    public static PedidoResponse from(Pedido p, EstadoPedido e, List<PedidoDetalleResponse> detalles,
                                      List<HistorialEstadoResponse> historial) {
        return new PedidoResponse(p.getId(), p.getCliente().getId(), p.getCliente().getNombres(),
                p.getEstadoCodigo(), e.getNombre(), p.getFechaSolicitada(), p.getDetallePersonalizacion(),
                p.getObservaciones(), p.getCreadoEn(), p.getActualizadoEn(), detalles, historial);
    }
}
