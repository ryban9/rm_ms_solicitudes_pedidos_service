package ec.rmdetalles.pedidos.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class EstadoPedidoId implements Serializable {
    private UUID negocioId;
    private String codigo;
    public EstadoPedidoId() { }
    public EstadoPedidoId(UUID negocioId, String codigo) { this.negocioId = negocioId; this.codigo = codigo; }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EstadoPedidoId that)) return false;
        return Objects.equals(negocioId, that.negocioId) && Objects.equals(codigo, that.codigo);
    }
    @Override public int hashCode() { return Objects.hash(negocioId, codigo); }
}
