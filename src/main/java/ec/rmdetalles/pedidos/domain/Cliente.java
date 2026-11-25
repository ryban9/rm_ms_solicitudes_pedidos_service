package ec.rmdetalles.pedidos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "clientes", schema = "rm_detalles")
public class Cliente {
    @Id private UUID id;
    @Column(name = "negocio_id", nullable = false) private UUID negocioId;
    @Column(nullable = false) private String nombres;
    @Column(nullable = false) private boolean activo;
    protected Cliente() { }
    public UUID getId() { return id; }
    public UUID getNegocioId() { return negocioId; }
    public String getNombres() { return nombres; }
    public boolean isActivo() { return activo; }
}
