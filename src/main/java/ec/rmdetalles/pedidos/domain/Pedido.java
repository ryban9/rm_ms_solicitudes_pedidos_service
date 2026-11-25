package ec.rmdetalles.pedidos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedidos", schema = "rm_detalles")
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "negocio_id", nullable = false, updatable = false) private Negocio negocio;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false) private Cliente cliente;
    @Column(name = "registrado_por") private UUID registradoPor;
    @Column(name = "estado_codigo", nullable = false, length = 30) private String estadoCodigo;
    @Column(name = "fecha_solicitada") private LocalDate fechaSolicitada;
    @Column(name = "detalle_personalizacion", columnDefinition = "text") private String detallePersonalizacion;
    @Column(columnDefinition = "text") private String observaciones;
    @CreationTimestamp @Column(name = "creado_en", nullable = false, updatable = false) private OffsetDateTime creadoEn;
    @UpdateTimestamp @Column(name = "actualizado_en", nullable = false) private OffsetDateTime actualizadoEn;
    protected Pedido() { }
    public Pedido(Negocio negocio, Cliente cliente, UUID registradoPor, String estadoCodigo,
                  LocalDate fechaSolicitada, String detallePersonalizacion, String observaciones) {
        this.negocio = negocio; this.cliente = cliente; this.registradoPor = registradoPor;
        this.estadoCodigo = estadoCodigo; this.fechaSolicitada = fechaSolicitada;
        this.detallePersonalizacion = detallePersonalizacion; this.observaciones = observaciones;
    }
    public UUID getId() { return id; }
    public Negocio getNegocio() { return negocio; }
    public Cliente getCliente() { return cliente; }
    public String getEstadoCodigo() { return estadoCodigo; }
    public LocalDate getFechaSolicitada() { return fechaSolicitada; }
    public String getDetallePersonalizacion() { return detallePersonalizacion; }
    public String getObservaciones() { return observaciones; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
    public void editar(Cliente cliente, LocalDate fecha, String personalizacion, String observaciones) {
        this.cliente = cliente; this.fechaSolicitada = fecha;
        this.detallePersonalizacion = personalizacion; this.observaciones = observaciones;
    }
    public void cambiarEstado(String estadoCodigo) { this.estadoCodigo = estadoCodigo; }
}
