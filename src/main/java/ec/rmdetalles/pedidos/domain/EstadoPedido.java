package ec.rmdetalles.pedidos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "estados_pedido", schema = "rm_detalles")
@IdClass(EstadoPedidoId.class)
public class EstadoPedido {
    @Id @Column(name = "negocio_id", nullable = false) private UUID negocioId;
    @Id @Column(nullable = false, length = 30) private String codigo;
    @Column(nullable = false, length = 80) private String nombre;
    @Column(nullable = false) private short orden;
    @Column(name = "es_inicial", nullable = false) private boolean inicial;
    @Column(name = "es_final", nullable = false) private boolean finalizado;
    @Column(nullable = false) private boolean activo;
    protected EstadoPedido() { }
    public UUID getNegocioId() { return negocioId; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public short getOrden() { return orden; }
    public boolean isInicial() { return inicial; }
    public boolean isFinalizado() { return finalizado; }
    public boolean isActivo() { return activo; }
}
