-- Inicializa estados y transiciones estándar para negocios activos que todavía
-- no tengan flujo. No elimina ni modifica estados, transiciones o permisos existentes.
BEGIN;

INSERT INTO rm_detalles.estados_pedido
    (negocio_id, codigo, nombre, orden, es_inicial, es_final, activo)
SELECT n.id, s.codigo, s.nombre, s.orden, s.es_inicial, s.es_final, TRUE
FROM rm_detalles.negocios n
CROSS JOIN (VALUES
    ('PENDIENTE', 'Pendiente', 0, TRUE, FALSE),
    ('CONFIRMADO', 'Confirmado', 1, FALSE, FALSE),
    ('EN_PREPARACION', 'En preparación', 2, FALSE, FALSE),
    ('LISTO', 'Listo', 3, FALSE, FALSE),
    ('ENTREGADO', 'Entregado', 4, FALSE, TRUE),
    ('CANCELADO', 'Cancelado', 5, FALSE, TRUE)
) AS s(codigo, nombre, orden, es_inicial, es_final)
WHERE n.activo = TRUE
  AND NOT EXISTS (SELECT 1 FROM rm_detalles.estados_pedido e WHERE e.negocio_id = n.id)
ON CONFLICT (negocio_id, codigo) DO NOTHING;

INSERT INTO rm_detalles.transiciones_pedido
    (negocio_id, estado_origen, estado_destino, activo)
SELECT n.id, t.origen, t.destino, TRUE
FROM rm_detalles.negocios n
CROSS JOIN (VALUES
    ('PENDIENTE', 'CONFIRMADO'),
    ('PENDIENTE', 'CANCELADO'),
    ('CONFIRMADO', 'EN_PREPARACION'),
    ('CONFIRMADO', 'CANCELADO'),
    ('EN_PREPARACION', 'LISTO'),
    ('EN_PREPARACION', 'CANCELADO'),
    ('LISTO', 'ENTREGADO')
) AS t(origen, destino)
WHERE n.activo = TRUE
  AND NOT EXISTS (SELECT 1 FROM rm_detalles.transiciones_pedido x WHERE x.negocio_id = n.id)
  AND EXISTS (SELECT 1 FROM rm_detalles.estados_pedido a WHERE a.negocio_id=n.id AND a.codigo=t.origen AND a.activo=TRUE)
  AND EXISTS (SELECT 1 FROM rm_detalles.estados_pedido b WHERE b.negocio_id=n.id AND b.codigo=t.destino AND b.activo=TRUE)
ON CONFLICT (negocio_id, estado_origen, estado_destino) DO NOTHING;

-- Otorga permisos de pedidos a roles existentes solo cuando todavía no existe
-- una decisión para esa pareja rol-permiso; respeta concesiones y revocaciones previas.
INSERT INTO rm_detalles.rol_permisos (rol_id, permiso_id, concedido)
SELECT r.id, p.id, TRUE
FROM rm_detalles.roles r
JOIN rm_detalles.permisos p ON p.codigo IN ('PEDIDOS_CONSULTAR','PEDIDOS_GESTIONAR','PEDIDOS_ESTADO_CAMBIAR')
WHERE r.activo = TRUE AND r.codigo = 'ADMINISTRADOR'
ON CONFLICT (rol_id, permiso_id) DO NOTHING;

INSERT INTO rm_detalles.rol_permisos (rol_id, permiso_id, concedido)
SELECT r.id, p.id, TRUE
FROM rm_detalles.roles r
JOIN rm_detalles.permisos p ON p.codigo IN ('PEDIDOS_CONSULTAR','PEDIDOS_GESTIONAR','PEDIDOS_ESTADO_CAMBIAR')
WHERE r.activo = TRUE AND r.codigo = 'VENDEDOR'
ON CONFLICT (rol_id, permiso_id) DO NOTHING;

COMMIT;
