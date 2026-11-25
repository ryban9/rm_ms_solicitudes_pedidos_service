package ec.rmdetalles.pedidos.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record DetalleSolicitudRequest(
        UUID productoId,
        @Size(max = 300) String descripcion,
        @Min(1) int cantidad,
        @DecimalMin("0.00") BigDecimal precioUnitario
) { }
