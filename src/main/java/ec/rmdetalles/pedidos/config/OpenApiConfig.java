package ec.rmdetalles.pedidos.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI apiContract() {
        return new OpenAPI()
                .info(new Info().title("solicitudes-pedidos-service").version("v1")
                        .description("Contrato para Angular. X-Negocio-Codigo selecciona el negocio activo. "
                                + "Las rutas privadas requieren JWT de Supabase, membresía y permiso. "
                                + "El Origin del navegador debe estar habilitado para el negocio."))
                .servers(List.of(new Server().url("http://localhost:8080")))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .scheme("bearer").bearerFormat("JWT")));
    }

    @Bean
    OpenApiCustomizer businessContract() {
        return api -> api.getPaths().forEach((path, item) ->
                item.readOperationsMap().forEach((method, operation) -> {
                    String permission = permission(method.name(), path);
                    if (operation.getSummary() == null) {
                        operation.setSummary(operation.getOperationId());
                    }
                    operation.addParametersItem(new Parameter().name("X-Negocio-Codigo")
                            .in("header").required(true).example("RMMODA")
                            .description("Código del negocio activo, por ejemplo RMD o RMMODA.")
                            .schema(new StringSchema()));
                    operation.addExtension("x-permission", permission);
                    operation.setDescription("Acceso requerido — " + permission + ".");
                    if (!"PUBLICO".equals(permission)) {
                        operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                        operation.getResponses().addApiResponse("401", new ApiResponse()
                                .description("JWT ausente, expirado o inválido. Puede incluir WWW-Authenticate y cuerpo vacío."));
                    }
                    operation.getResponses().addApiResponse("400", errorResponse()
                            .description("Cabecera de negocio ausente, JSON o datos inválidos."));
                    operation.getResponses().addApiResponse("403", errorResponse()
                            .description("Membresía, permiso u origen no habilitado."));
                    operation.getResponses().addApiResponse("404", errorResponse()
                            .description("Negocio activo o recurso del negocio no encontrado."));
                    if (!"GET".equals(method.name())) {
                        operation.getResponses().addApiResponse("409", errorResponse()
                                .description("Conflicto de datos o regla de negocio."));
                    }
                    if (path.contains("imagenes") && "POST".equals(method.name())) {
                        operation.getResponses().addApiResponse("413", errorResponse()
                                .description("Tamaño superior al límite del negocio o del servicio."));
                    }
                }));
    }

    private ApiResponse errorResponse() {
        ObjectSchema problem = new ObjectSchema();
        problem.addProperty("type", new StringSchema());
        problem.addProperty("title", new StringSchema());
        problem.addProperty("status", new IntegerSchema());
        problem.addProperty("detail", new StringSchema());
        problem.addProperty("instance", new StringSchema());
        ObjectSchema filterError = new ObjectSchema();
        filterError.addProperty("status", new IntegerSchema());
        filterError.addProperty("detail", new StringSchema());
        return new ApiResponse().content(new Content()
                .addMediaType("application/problem+json", new MediaType().schema(problem))
                .addMediaType("application/json", new MediaType().schema(filterError)));
    }

    private String permission(String method, String path) {
        if (("POST".equals(method) && path.endsWith("/convertir"))
                || ("PUT".equals(method) && path.endsWith("/estado"))) {
            return "PEDIDOS_ESTADO_CAMBIAR";
        }
        return "GET".equals(method) ? "PEDIDOS_CONSULTAR" : "PEDIDOS_GESTIONAR";
    }
}
