package ec.rmdetalles.pedidos.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SolicitudRequest(
        UUID clienteId,
        LocalDate fechaSolicitada,
        @Size(max = 10000) String detallePersonalizacion,
        @Size(max = 10000) String observaciones,
        @NotEmpty List<@Valid DetalleSolicitudRequest> detalles
) { }
