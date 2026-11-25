package ec.rmdetalles.pedidos.api;

import ec.rmdetalles.pedidos.domain.EstadoPedido;

public record EstadoPedidoResponse(String codigo, String nombre, short orden, boolean inicial, boolean finalizado) {
    public static EstadoPedidoResponse from(EstadoPedido e) {
        return new EstadoPedidoResponse(e.getCodigo(), e.getNombre(), e.getOrden(), e.isInicial(), e.isFinalizado());
    }
}
