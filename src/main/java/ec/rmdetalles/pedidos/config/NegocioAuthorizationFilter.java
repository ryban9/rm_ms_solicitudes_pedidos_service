package ec.rmdetalles.pedidos.config;

import ec.rmdetalles.pedidos.repository.AccesoNegocioProjection;
import ec.rmdetalles.pedidos.repository.AccesoNegocioRepository;
import ec.rmdetalles.pedidos.repository.FrontendNegocioRepository;
import ec.rmdetalles.pedidos.repository.NegocioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class NegocioAuthorizationFilter extends OncePerRequestFilter {
    private final AccesoNegocioRepository accesos;
    private final NegocioRepository negocios;
    private final FrontendNegocioRepository frontends;
    public NegocioAuthorizationFilter(AccesoNegocioRepository accesos, NegocioRepository negocios, FrontendNegocioRepository frontends) {
        this.accesos=accesos; this.negocios=negocios; this.frontends=frontends;
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String path=request.getRequestURI();
        if (path.startsWith("/actuator/health") || "OPTIONS".equals(request.getMethod())) { chain.doFilter(request,response); return; }
        String codigo=request.getHeader("X-Negocio-Codigo");
        if (codigo==null || codigo.isBlank()) { error(response,400,"Falta el encabezado X-Negocio-Codigo."); return; }
        codigo=codigo.trim().toUpperCase(Locale.ROOT);
        var negocio=negocios.findByCodigoAndActivoTrue(codigo);
        if (negocio.isEmpty()) { error(response,404,"No se encontró un negocio activo con ese código."); return; }
        String origin=request.getHeader("Origin");
        if (origin!=null && !origin.isBlank() && !frontends.existeActivoPorNegocioYOrigen(codigo,origin)) {
            error(response,403,"Este origen no está habilitado para este negocio."); return;
        }
        NegocioContext.set(negocio.get().getId());
        try {
            var auth=SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwt) {
                UUID usuarioId;
                try { usuarioId=UUID.fromString(jwt.getToken().getSubject()); }
                catch (RuntimeException ex) { error(response,401,"El identificador del usuario no es válido."); return; }
                List<AccesoNegocioProjection> filas=accesos.buscarAcceso(usuarioId,codigo);
                if (filas.isEmpty()) { error(response,403,"El usuario no tiene acceso a este negocio."); return; }
                Set<GrantedAuthority> authorities=new LinkedHashSet<>();
                for (var fila:filas) {
                    if (fila.getRolCodigo()!=null) authorities.add(new SimpleGrantedAuthority("ROLE_"+fila.getRolCodigo()));
                    if (fila.getPermisoCodigo()!=null) authorities.add(new SimpleGrantedAuthority(fila.getPermisoCodigo()));
                }
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt.getToken(),authorities,jwt.getName()));
            }
            chain.doFilter(request,response);
        } finally { NegocioContext.clear(); }
    }
    private void error(HttpServletResponse response, int status, String detail) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"status\":" + status + ",\"detail\":\"" + detail + "\"}"
        );
    }
}
