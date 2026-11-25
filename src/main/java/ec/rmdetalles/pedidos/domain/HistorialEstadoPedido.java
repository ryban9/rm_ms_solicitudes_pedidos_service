package ec.rmdetalles.pedidos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_estados_pedido", schema = "rm_detalles")
public class HistorialEstadoPedido {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "negocio_id", nullable = false, updatable = false) private UUID negocioId;
    @Column(name = "pedido_id", nullable = false, updatable = false) private UUID pedidoId;
    @Column(name = "estado_origen", length = 30) private String estadoOrigen;
    @Column(name = "estado_destino", nullable = false, length = 30) private String estadoDestino;
    @Column(name = "cambiado_por") private UUID cambiadoPor;
    @Column(length = 500) private String comentario;
    @CreationTimestamp @Column(name = "creado_en", nullable = false, updatable = false) private OffsetDateTime creadoEn;
    protected HistorialEstadoPedido() { }
    public HistorialEstadoPedido(UUID negocioId, UUID pedidoId, String origen, String destino, UUID usuarioId, String comentario) {
        this.negocioId=negocioId; this.pedidoId=pedidoId; this.estadoOrigen=origen; this.estadoDestino=destino;
        this.cambiadoPor=usuarioId; this.comentario=comentario;
    }
    public String getEstadoOrigen() { return estadoOrigen; }
    public String getEstadoDestino() { return estadoDestino; }
    public String getComentario() { return comentario; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
}
