package ec.rmdetalles.pedidos.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarEstadoRequest(
        @NotBlank @Size(max = 30) String estadoDestino,
        @Size(max = 500) String comentario
) { }
