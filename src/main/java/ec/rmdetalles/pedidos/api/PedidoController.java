package ec.rmdetalles.pedidos.api;

import ec.rmdetalles.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PedidoController {
    private final PedidoService service;
    public PedidoController(PedidoService service) { this.service = service; }

    @GetMapping("/solicitudes")
    public List<PedidoResponse> solicitudes() { return service.listarSolicitudes(); }

    @PostMapping("/solicitudes")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crearSolicitud(@Valid @RequestBody SolicitudRequest request) { return service.crearSolicitud(request); }

    @GetMapping("/solicitudes/{id}")
    public PedidoResponse obtenerSolicitud(@PathVariable UUID id) { return service.obtenerSolicitud(id); }

    @PutMapping("/solicitudes/{id}")
    public PedidoResponse actualizarSolicitud(@PathVariable UUID id, @Valid @RequestBody SolicitudRequest request) {
        return service.actualizarSolicitud(id, request);
    }

    @PostMapping("/solicitudes/{id}/convertir")
    public PedidoResponse convertir(@PathVariable UUID id, @Valid @RequestBody(required = false) ConvertirSolicitudRequest request) {
        return service.convertir(id, request == null ? null : request.comentario());
    }

    @GetMapping("/pedidos")
    public List<PedidoResponse> pedidos() { return service.listarPedidos(); }

    @GetMapping("/pedidos/{id}")
    public PedidoResponse obtenerPedido(@PathVariable UUID id) { return service.obtenerPedido(id); }

    @PutMapping("/pedidos/{id}/estado")
    public PedidoResponse cambiarEstado(@PathVariable UUID id, @Valid @RequestBody CambiarEstadoRequest request) {
        return service.cambiarEstado(id, request.estadoDestino(), request.comentario());
    }
}
