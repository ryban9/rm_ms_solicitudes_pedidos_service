package ec.rmdetalles.pedidos.api;

import ec.rmdetalles.pedidos.domain.HistorialEstadoPedido;
import java.time.OffsetDateTime;

public record HistorialEstadoResponse(String estadoOrigen, String estadoDestino, String comentario, OffsetDateTime creadoEn) {
    public static HistorialEstadoResponse from(HistorialEstadoPedido h) {
        return new HistorialEstadoResponse(h.getEstadoOrigen(), h.getEstadoDestino(), h.getComentario(), h.getCreadoEn());
    }
}
