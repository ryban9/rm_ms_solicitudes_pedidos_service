# solicitudes-pedidos-service

Microservicio Spring Boot para solicitudes personalizadas y pedidos, aislado por negocio en `rm_detalles`. Usa la estructura multiempresa ya creada en Supabase; no crea ni elimina tablas. Requiere que existan `pedidos`, `pedido_detalles`, `historial_estados_pedido`, `estados_pedido`, `transiciones_pedido`, `negocios`, `usuarios`, `usuario_negocios`, `roles`, `permisos`, `rol_permisos`, `clientes` y `productos`.

## Comportamiento

- Una solicitud se guarda como una fila de `pedidos` en el único estado inicial activo configurado para el negocio.
- `POST /solicitudes/{id}/convertir` avanza por la transición permitida de menor orden desde el estado inicial. Con el flujo inicial, pasa de `PENDIENTE` a `CONFIRMADO`.
- Los estados visibles y las transiciones válidas se leen de `estados_pedido` y `transiciones_pedido`, por negocio. Para cambiar el flujo se editan esas tablas; no se cambian constantes de Java.
- La conversión cambia el estado de la fila existente; no duplica el pedido ni crea otra tabla.
- El historial de creación y cada cambio de estado se conserva en `historial_estados_pedido`.
- Cada petición exige `X-Negocio-Codigo` y un JWT de Supabase. Revisa la membresía y los permisos del negocio; filtra lecturas y escrituras por su UUID.
- No registra ventas ni descuenta inventario al convertir o cambiar estados. Esas acciones pertenecen a los microservicios de ventas e inventario.

## Requisitos previos

1. Java 21.
2. PostgreSQL/Supabase configurado con el esquema `rm_detalles`.
3. Ejecutar una sola vez, si el negocio todavía no tiene flujo de estados, el script `src/main/resources/db/seed_pedidos_defaults.sql` desde SQL Editor de Supabase. El script rellena estados/transiciones iniciales solo para negocios sin configuración y añade permisos a roles existentes solo cuando no hay una asignación previa. No borra ni actualiza asignaciones existentes.
4. En IntelliJ, configurar las variables que ya usas en los otros microservicios:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `SUPABASE_ISSUER_URI`
   - `SUPABASE_JWK_SET_URI`

La aplicación valida el esquema con `ddl-auto: validate`; si falta una tabla/columna, lo informa al iniciar en vez de crearla automáticamente.

## Ejecutar

Importar como proyecto Maven en IntelliJ y ejecutar `PedidosServiceApplication`. Por defecto escucha en `http://localhost:8084`.

En Postman, configura la autorización Bearer Token con `{{TOKEN}}` y añade a cada solicitud:

```http
X-Negocio-Codigo: RMD
Content-Type: application/json
```

Usa `RMMODA` para comprobar el aislamiento del otro negocio. El usuario del token debe tener membresía activa y el permiso correspondiente en ese negocio.

## Endpoints

| Método | Ruta | Permiso | Uso |
|---|---|---|---|
| GET | `/actuator/health` | Público | Salud del servicio |
| GET | `/api/v1/pedido-configuracion/estados` | `PEDIDOS_CONSULTAR` | Ver estados activos del negocio |
| GET | `/api/v1/solicitudes` | `PEDIDOS_CONSULTAR` | Listar registros en el estado inicial |
| POST | `/api/v1/solicitudes` | `PEDIDOS_GESTIONAR` | Crear solicitud personalizada |
| GET | `/api/v1/solicitudes/{id}` | `PEDIDOS_CONSULTAR` | Consultar solicitud pendiente |
| PUT | `/api/v1/solicitudes/{id}` | `PEDIDOS_GESTIONAR` | Editar solicitud mientras esté en estado inicial |
| POST | `/api/v1/solicitudes/{id}/convertir` | `PEDIDOS_ESTADO_CAMBIAR` | Convertirla al primer estado permitido siguiente |
| GET | `/api/v1/pedidos` | `PEDIDOS_CONSULTAR` | Listar registros fuera del estado inicial |
| GET | `/api/v1/pedidos/{id}` | `PEDIDOS_CONSULTAR` | Consultar pedido e historial |
| PUT | `/api/v1/pedidos/{id}/estado` | `PEDIDOS_ESTADO_CAMBIAR` | Cambiar al estado indicado si la transición está habilitada |

## Probar en Postman

1. `GET http://localhost:8084/api/v1/pedido-configuracion/estados`. Debe devolver estados para el negocio indicado.
2. Crea una solicitud con un `clienteId` activo que pertenezca al mismo negocio:

```json
{
  "clienteId": "{{CLIENTE_ID}}",
  "fechaSolicitada": "2026-12-15",
  "detallePersonalizacion": "Ramo de flores preservadas en tonos crema",
  "observaciones": "Retiro por el local",
  "detalles": [
    {
      "productoId": null,
      "descripcion": "Ramo personalizado mediano",
      "cantidad": 1,
      "precioUnitario": 25.00
    }
  ]
}
```

Respuesta esperada: `201 Created`, estado inicial, detalle e historial.

3. `GET /api/v1/solicitudes` debe incluirla. Prueba `GET /api/v1/solicitudes/{{SOLICITUD_ID}}`.
4. Ejecuta `POST /api/v1/solicitudes/{{SOLICITUD_ID}}/convertir` sin body. Con los estados iniciales, pasa a `CONFIRMADO`.
5. `GET /api/v1/solicitudes` ya no debe incluirla y `GET /api/v1/pedidos` debe incluirla.
6. Avanza con `PUT /api/v1/pedidos/{{PEDIDO_ID}}/estado`:

```json
{
  "estadoDestino": "EN_PREPARACION",
  "comentario": "Se inició la elaboración"
}
```

Solo se aceptan destinos con transición activa en la base.
7. Prueba aislamiento: con el mismo UUID y `X-Negocio-Codigo` de otro negocio, las consultas deben dar `404`. Un cliente o producto de otro negocio no debe aceptarse al registrar solicitud.

Respuestas de validación y transiciones inválidas son `400`; falta de permisos produce `403`; registro inexistente o de otro negocio, `404`.
