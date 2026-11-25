package ec.rmdetalles.pedidos.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class TransicionPedidoId implements Serializable {
    private UUID negocioId;
    private String estadoOrigen;
    private String estadoDestino;
    public TransicionPedidoId() { }
    public TransicionPedidoId(UUID negocioId, String estadoOrigen, String estadoDestino) {
        this.negocioId = negocioId; this.estadoOrigen = estadoOrigen; this.estadoDestino = estadoDestino;
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransicionPedidoId that)) return false;
        return Objects.equals(negocioId, that.negocioId) && Objects.equals(estadoOrigen, that.estadoOrigen)
                && Objects.equals(estadoDestino, that.estadoDestino);
    }
    @Override public int hashCode() { return Objects.hash(negocioId, estadoOrigen, estadoDestino); }
}
