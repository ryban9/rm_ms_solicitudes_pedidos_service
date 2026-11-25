package ec.rmdetalles.pedidos.config;

import ec.rmdetalles.pedidos.repository.AccesoNegocioRepository;
import ec.rmdetalles.pedidos.repository.FrontendNegocioRepository;
import ec.rmdetalles.pedidos.repository.NegocioRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean NegocioAuthorizationFilter negocioAuthorizationFilter(AccesoNegocioRepository accesos, NegocioRepository negocios, FrontendNegocioRepository frontends) {
        return new NegocioAuthorizationFilter(accesos,negocios,frontends);
    }
    @Bean FilterRegistrationBean<NegocioAuthorizationFilter> disableContainerRegistration(NegocioAuthorizationFilter filter) {
        FilterRegistrationBean<NegocioAuthorizationFilter> bean=new FilterRegistrationBean<>(filter); bean.setEnabled(false); return bean;
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, NegocioAuthorizationFilter filter) throws Exception {
        http.csrf(c -> c.disable()).cors(Customizer.withDefaults())
            .authorizeHttpRequests(a -> a
                .requestMatchers("/actuator/health","/actuator/health/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/v1/solicitudes/*/convertir").hasAuthority("PEDIDOS_ESTADO_CAMBIAR")
                .requestMatchers(HttpMethod.PUT,"/api/v1/pedidos/*/estado").hasAuthority("PEDIDOS_ESTADO_CAMBIAR")
                .requestMatchers(HttpMethod.GET,"/api/v1/**").hasAuthority("PEDIDOS_CONSULTAR")
                .requestMatchers(HttpMethod.POST,"/api/v1/solicitudes").hasAuthority("PEDIDOS_GESTIONAR")
                .requestMatchers(HttpMethod.PUT,"/api/v1/solicitudes/*").hasAuthority("PEDIDOS_GESTIONAR")
                .anyRequest().denyAll())
            .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
            .addFilterAfter(filter,BearerTokenAuthenticationFilter.class);
        return http.build();
    }
    @Bean CorsConfigurationSource corsConfigurationSource(FrontendNegocioRepository frontends) {
        return request -> {
            String origin=request.getHeader("Origin");
            if (origin==null || origin.isBlank() || !frontends.existeOrigenActivo(origin)) return null;
            CorsConfiguration c=new CorsConfiguration(); c.setAllowedOrigins(List.of(origin));
            c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
            c.setAllowedHeaders(List.of("Authorization","Content-Type","X-Negocio-Codigo"));
            c.setExposedHeaders(List.of("Location")); c.setAllowCredentials(true); return c;
        };
    }
}
