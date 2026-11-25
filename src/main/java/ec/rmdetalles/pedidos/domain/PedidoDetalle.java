package ec.rmdetalles.pedidos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedido_detalles", schema = "rm_detalles")
public class PedidoDetalle {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "negocio_id", nullable = false, updatable = false) private UUID negocioId;
    @Column(name = "pedido_id", nullable = false, updatable = false) private UUID pedidoId;
    @Column(name = "producto_id") private UUID productoId;
    @Column(nullable = false, length = 300) private String descripcion;
    @Column(nullable = false) private int cantidad;
    @Column(name = "precio_unitario", precision = 12, scale = 2) private BigDecimal precioUnitario;
    @CreationTimestamp @Column(name = "creado_en", nullable = false, updatable = false) private OffsetDateTime creadoEn;
    protected PedidoDetalle() { }
    public PedidoDetalle(UUID negocioId, UUID pedidoId, UUID productoId, String descripcion, int cantidad, BigDecimal precioUnitario) {
        this.negocioId = negocioId; this.pedidoId = pedidoId; this.productoId = productoId;
        this.descripcion = descripcion; this.cantidad = cantidad; this.precioUnitario = precioUnitario;
    }
    public UUID getId() { return id; }
    public UUID getProductoId() { return productoId; }
    public String getDescripcion() { return descripcion; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
}
