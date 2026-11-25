package ec.rmdetalles.pedidos.service;

import ec.rmdetalles.pedidos.api.DetalleSolicitudRequest;
import ec.rmdetalles.pedidos.api.HistorialEstadoResponse;
import ec.rmdetalles.pedidos.api.PedidoDetalleResponse;
import ec.rmdetalles.pedidos.api.PedidoResponse;
import ec.rmdetalles.pedidos.api.SolicitudRequest;
import ec.rmdetalles.pedidos.config.NegocioContext;
import ec.rmdetalles.pedidos.domain.*;
import ec.rmdetalles.pedidos.repository.ClienteRepository;
import ec.rmdetalles.pedidos.repository.EstadoPedidoRepository;
import ec.rmdetalles.pedidos.repository.HistorialEstadoPedidoRepository;
import ec.rmdetalles.pedidos.repository.NegocioRepository;
import ec.rmdetalles.pedidos.repository.PedidoDetalleRepository;
import ec.rmdetalles.pedidos.repository.PedidoRepository;
import ec.rmdetalles.pedidos.repository.ProductoRepository;
import ec.rmdetalles.pedidos.repository.TransicionPedidoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Locale;

@Service
    @Transactional
public class PedidoService {
    private final PedidoRepository pedidos;
    private final PedidoDetalleRepository detalles;
    private final ClienteRepository clientes;
    private final ProductoRepository productos;
    private final EstadoPedidoRepository estados;
    private final TransicionPedidoRepository transiciones;
    private final HistorialEstadoPedidoRepository historial;
    private final NegocioRepository negocios;

    public PedidoService(PedidoRepository pedidos, PedidoDetalleRepository detalles,
                         ClienteRepository clientes, ProductoRepository productos,
                         EstadoPedidoRepository estados, TransicionPedidoRepository transiciones,
                         HistorialEstadoPedidoRepository historial, NegocioRepository negocios) {
        this.pedidos=pedidos; this.detalles=detalles; this.clientes=clientes; this.productos=productos;
        this.estados=estados; this.transiciones=transiciones; this.historial=historial; this.negocios=negocios;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarSolicitudes() {
        UUID negocioId=NegocioContext.getRequired();
        String inicial=estadoInicial(negocioId).getCodigo();
        return pedidos.findByNegocio_IdAndEstadoCodigoOrderByCreadoEnDesc(negocioId, inicial)
                .stream().map(this::toResponse).toList();
    }

    public PedidoResponse crearSolicitud(SolicitudRequest request) {
        UUID negocioId=NegocioContext.getRequired();
        Cliente cliente=resolverCliente(request.clienteId(), negocioId);
        EstadoPedido inicial=estadoInicial(negocioId);
        Pedido pedido=pedidos.save(new Pedido(negocios.getReferenceById(negocioId), cliente, usuarioActual(),
                inicial.getCodigo(), request.fechaSolicitada(), limpiar(request.detallePersonalizacion()),
                limpiar(request.observaciones())));
        guardarDetalles(pedido, request.detalles(), negocioId);
        historial.save(new HistorialEstadoPedido(negocioId, pedido.getId(), null, inicial.getCodigo(), usuarioActual(), "Solicitud registrada"));
        return toResponse(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtenerSolicitud(UUID id) {
        UUID negocioId=NegocioContext.getRequired();
        Pedido pedido=pedidos.findByIdAndNegocio_IdAndEstadoCodigo(id, negocioId, estadoInicial(negocioId).getCodigo())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud."));
        return toResponse(pedido);
    }

    public PedidoResponse actualizarSolicitud(UUID id, SolicitudRequest request) {
        UUID negocioId=NegocioContext.getRequired();
        Pedido pedido=pedidos.findByIdAndNegocio_IdAndEstadoCodigo(id, negocioId, estadoInicial(negocioId).getCodigo())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud pendiente."));
        pedido.editar(resolverCliente(request.clienteId(), negocioId), request.fechaSolicitada(),
                limpiar(request.detallePersonalizacion()), limpiar(request.observaciones()));
        detalles.eliminarPorPedido(negocioId, id);
        guardarDetalles(pedido, request.detalles(), negocioId);
        return toResponse(pedido);
    }

    public PedidoResponse convertir(UUID id, String comentario) {
        UUID negocioId=NegocioContext.getRequired();
        Pedido pedido=pedidos.findByIdAndNegocio_IdAndEstadoCodigo(id, negocioId, estadoInicial(negocioId).getCodigo())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud pendiente."));
        List<TransicionPedido> salidas=transiciones.findByNegocioIdAndEstadoOrigenAndActivoTrue(negocioId, pedido.getEstadoCodigo());
        if (salidas.isEmpty()) throw new IllegalStateException("No hay una transición habilitada para convertir esta solicitud.");
        EstadoPedido destino=salidas.stream().map(t -> estados.findByNegocioIdAndCodigoAndActivoTrue(negocioId, t.getEstadoDestino()).orElse(null))
                .filter(e -> e != null && !e.isFinalizado()).min((a,b) -> Short.compare(a.getOrden(),b.getOrden()))
                .orElseThrow(() -> new IllegalStateException("No se encontró un estado de conversión habilitado."));
        aplicarCambio(pedido, destino, comentario == null ? "Solicitud convertida en pedido" : comentario);
        return toResponse(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPedidos() {
        UUID negocioId=NegocioContext.getRequired();
        String inicial=estadoInicial(negocioId).getCodigo();
        return pedidos.findByNegocio_IdAndEstadoCodigoNotOrderByCreadoEnDesc(negocioId, inicial)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtenerPedido(UUID id) {
        UUID negocioId=NegocioContext.getRequired();
        String inicial=estadoInicial(negocioId).getCodigo();
        Pedido pedido=pedidos.findByIdAndNegocio_Id(id, negocioId)
                .filter(p -> !p.getEstadoCodigo().equals(inicial))
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido."));
        return toResponse(pedido);
    }

    public PedidoResponse cambiarEstado(UUID id, String codigoDestino, String comentario) {
        UUID negocioId=NegocioContext.getRequired();
        String estadoInicial=estadoInicial(negocioId).getCodigo();
        Pedido pedido=pedidos.findByIdAndNegocio_Id(id, negocioId)
                .filter(p -> !p.getEstadoCodigo().equals(estadoInicial))
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido."));
        EstadoPedido destino=estados.findByNegocioIdAndCodigoAndActivoTrue(negocioId, codigoDestino.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException("El estado indicado no está configurado para este negocio."));
        aplicarCambio(pedido, destino, comentario);
        return toResponse(pedido);
    }

    private void aplicarCambio(Pedido pedido, EstadoPedido destino, String comentario) {
        UUID negocioId=NegocioContext.getRequired();
        String origen=pedido.getEstadoCodigo();
        if (origen.equals(destino.getCodigo())) throw new IllegalArgumentException("El pedido ya tiene ese estado.");
        if (transiciones.findByNegocioIdAndEstadoOrigenAndEstadoDestinoAndActivoTrue(negocioId, origen, destino.getCodigo()).isEmpty())
            throw new IllegalArgumentException("La transición de estado no está permitida para este negocio.");
        pedido.cambiarEstado(destino.getCodigo());
        historial.save(new HistorialEstadoPedido(negocioId, pedido.getId(), origen, destino.getCodigo(), usuarioActual(), limpiar(comentario)));
    }

    private void guardarDetalles(Pedido pedido, List<DetalleSolicitudRequest> items, UUID negocioId) {
        List<PedidoDetalle> nuevos=new ArrayList<>();
        for (DetalleSolicitudRequest item : items) {
            UUID productoId=item.productoId();
            String descripcion=item.descripcion() == null ? "" : item.descripcion().trim();
            if (productoId != null) {
                Producto producto=productos.findByIdAndNegocioIdAndActivoTrue(productoId, negocioId)
                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto activo indicado en el negocio."));
                if (descripcion.isBlank()) descripcion=producto.getNombre();
            } else if (descripcion.isBlank()) {
                throw new IllegalArgumentException("La descripción es obligatoria cuando el detalle no se vincula a un producto.");
            }
            nuevos.add(new PedidoDetalle(negocioId, pedido.getId(), productoId, descripcion,
                    item.cantidad(), item.precioUnitario()));
        }
        detalles.saveAll(nuevos);
    }

    private Cliente resolverCliente(UUID id, UUID negocioId) {
        if (id == null) throw new IllegalArgumentException("clienteId es obligatorio para registrar una solicitud.");
        return clientes.findByIdAndNegocioIdAndActivoTrue(id, negocioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el cliente activo en este negocio."));
    }

    private EstadoPedido estadoInicial(UUID negocioId) {
        List<EstadoPedido> iniciales=estados.findByNegocioIdAndActivoTrueAndInicialTrue(negocioId);
        if (iniciales.size() != 1) throw new IllegalStateException("El negocio debe tener exactamente un estado inicial activo configurado.");
        return iniciales.getFirst();
    }

    private UUID usuarioActual() {
        var authentication=SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken token) {
            try { return UUID.fromString(token.getToken().getSubject()); }
            catch (RuntimeException ignored) { return null; }
        }
        return null;
    }

    private PedidoResponse toResponse(Pedido pedido) {
        UUID negocioId=NegocioContext.getRequired();
        EstadoPedido estado=estados.findByNegocioIdAndCodigoAndActivoTrue(negocioId, pedido.getEstadoCodigo())
                .orElseThrow(() -> new IllegalStateException("El estado del pedido no está activo."));
        List<PedidoDetalleResponse> detallesResponse=detalles.findByNegocioIdAndPedidoIdOrderByIdAsc(negocioId,pedido.getId())
                .stream().map(PedidoDetalleResponse::from).toList();
        List<HistorialEstadoResponse> historialResponse=historial.findByNegocioIdAndPedidoIdOrderByCreadoEnAsc(negocioId,pedido.getId())
                .stream().map(HistorialEstadoResponse::from).toList();
        return PedidoResponse.from(pedido,estado,detallesResponse,historialResponse);
    }
    private String limpiar(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
