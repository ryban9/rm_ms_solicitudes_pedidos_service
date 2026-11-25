package ec.rmdetalles.pedidos.config;

import java.util.UUID;

public final class NegocioContext {
    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();
    private NegocioContext() { }
    public static void set(UUID id) { CURRENT.set(id); }
    public static UUID getRequired() {
        UUID id = CURRENT.get();
        if (id == null) throw new IllegalStateException("No hay negocio asociado a la solicitud.");
        return id;
    }
    public static void clear() { CURRENT.remove(); }
}
