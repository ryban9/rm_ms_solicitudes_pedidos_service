package ec.rmdetalles.pedidos.api;

import ec.rmdetalles.pedidos.domain.PedidoDetalle;
import java.math.BigDecimal;
import java.util.UUID;

public record PedidoDetalleResponse(UUID id, UUID productoId, String descripcion, int cantidad, BigDecimal precioUnitario) {
    public static PedidoDetalleResponse from(PedidoDetalle d) {
        return new PedidoDetalleResponse(d.getId(), d.getProductoId(), d.getDescripcion(), d.getCantidad(), d.getPrecioUnitario());
    }
}
