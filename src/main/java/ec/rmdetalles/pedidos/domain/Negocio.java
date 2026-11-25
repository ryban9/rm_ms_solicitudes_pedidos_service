package ec.rmdetalles.pedidos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "negocios", schema = "rm_detalles")
public class Negocio {
    @Id private UUID id;
    @Column(nullable = false, unique = true, length = 60) private String codigo;
    @Column(nullable = false) private boolean activo;
    protected Negocio() { }
    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
}
