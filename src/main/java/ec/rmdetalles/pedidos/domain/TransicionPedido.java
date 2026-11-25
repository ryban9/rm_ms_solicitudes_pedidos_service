package ec.rmdetalles.pedidos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "transiciones_pedido", schema = "rm_detalles")
@IdClass(TransicionPedidoId.class)
public class TransicionPedido {
    @Id @Column(name = "negocio_id", nullable = false) private UUID negocioId;
    @Id @Column(name = "estado_origen", nullable = false, length = 30) private String estadoOrigen;
    @Id @Column(name = "estado_destino", nullable = false, length = 30) private String estadoDestino;
    @Column(nullable = false) private boolean activo;
    protected TransicionPedido() { }
    public String getEstadoOrigen() { return estadoOrigen; }
    public String getEstadoDestino() { return estadoDestino; }
}
