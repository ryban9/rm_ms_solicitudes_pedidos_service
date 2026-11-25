package ec.rmdetalles.pedidos.api;

import jakarta.validation.constraints.Size;

public record ConvertirSolicitudRequest(@Size(max = 500) String comentario) { }
