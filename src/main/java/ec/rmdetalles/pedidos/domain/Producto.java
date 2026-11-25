package ec.rmdetalles.pedidos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "productos", schema = "rm_detalles")
public class Producto {
    @Id private UUID id;
    @Column(name = "negocio_id", nullable = false) private UUID negocioId;
    @Column(nullable = false) private String nombre;
    @Column(nullable = false) private boolean activo;
    protected Producto() { }
    public UUID getId() { return id; }
    public UUID getNegocioId() { return negocioId; }
    public String getNombre() { return nombre; }
    public boolean isActivo() { return activo; }
}
