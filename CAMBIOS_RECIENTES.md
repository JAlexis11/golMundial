# Cambios recientes — Backend de Estadísticas

Resumen de lo tocado en esta sesión, pensado para ayudar a diagnosticar por qué el **Frontend Público** (Dayana, JSF+PrimeFaces) es el único que no logra conectarse. Complementa a `INFORME_MAESTRO_ESTADISTICAS.md`, que sigue siendo el documento completo del backend.

---

## 1. Bugs corregidos en el código

Ninguno de estos afecta la conectividad en sí, pero son cambios reales en el comportamiento de la API — si Dayana ya integró contra una versión anterior del backend, importa que sepa qué cambió:

| Archivo | Qué cambió |
|---|---|
| `SeleccionRepository.java`, `PartidoRepository.java` | `eliminar()` ahora devuelve **404** si el id no existe (antes devolvía 200 sin hacer nada). |
| `UsuarioRepository.java` | `actualizarRol()` ahora devuelve **404** si el usuario no existe (antes 200 sin hacer nada). |
| `SeleccionService.java`, `PartidoService.java` | `actualizar()` ahora valida que el `id` venga y que exista **antes** de guardar — si no, **404**. Antes, un `PUT` sin id o con id inexistente insertaba una fila nueva en silencio. |
| `PartidoService.java` | `eliminar()` ahora rechaza con **400** si el partido ya tiene un `Resultado` cargado (antes rompía contra la base con un error crudo). |
| `GlobalExceptionMapper.java` | Los errores **500** (no controlados) ya no devuelven el mensaje interno crudo al cliente — devuelven un mensaje genérico y el detalle queda logueado del lado del servidor. Los errores 400/401/403/404 (validaciones de negocio) siguen devolviendo el mensaje específico, sin cambios ahí. |

**Impacto para Dayana**: si su frontend depende de que un `PUT`/`DELETE` con un id inválido devuelva 200 "silencioso", eso ya no pasa — ahora es 404. Y si estaba mostrando el `mensaje` de un error 500 esperando ver detalle técnico, ahora va a ver un mensaje genérico ("Ocurrió un error inesperado en el servidor").

## 2. CRUD de `Partido` completo

`POST` / `PUT` / `DELETE` en `/api/partidos` (antes solo estaba el `GET`). Requieren rol `ADMINISTRADOR`. Detalle completo en la sección 6 del informe maestro.

## 3. Seed: fechas de partidos movidas a julio 2026

Las 72 fechas de partidos de `seed_mundial2026.sql` se corrieron de junio a **julio 2026** (mismos días 11-27, mismos horarios) para que el calendario caiga en el mes en curso durante las pruebas. Si Dayana ya cargó el seed viejo, sus datos de calendario van a mostrar junio — necesita recargar el seed actualizado (o correrle un `UPDATE` a mano) para ver julio.

## 4. Usuarios de prueba agregados

4 usuarios nuevos (`usuario1@utn.edu`, `usuario2@utn.edu`, `invitado1@utn.edu`, `admin2@utn.edu`, todos con contraseña `Prueba123!`) agregados al seed y a la base, con roles distintos para poder probar los tres niveles de permiso.

---

## 5. Cómo se conecta el Frontend Público a este backend

Esto es lo que **no cambió** en esta sesión, pero es la info que Dayana necesita:

- **Base URL**: `http://<tu-IP-de-red>:8080/estadisticas/api` (confirmá tu IP actual con `ipconfig` — la del Wi-Fi, no la de adaptadores virtuales; puede cambiar entre sesiones por DHCP).
- **CORS**: ya está abierto a cualquier origen (`Access-Control-Allow-Origin: *`, `security/CorsFilter.java`), y `AuthFilter` deja pasar el preflight `OPTIONS` sin pedir token. Si el error es de CORS, no es este backend — revisar si su llamada va por el lado del cliente (browser) o del servidor (ver sección 6).
- **Lectura pública**: cualquier `GET` (salvo `/api/usuarios`) no necesita token — Dayana puede consumir calendario/grupos/posiciones/selecciones/sedes/estadísticas sin login.
- **Escritura**: no aplica al Frontend Público — eso es solo para el admin (Fer/Blazor).

## 6. Pistas para el problema de conexión de Dayana

No tengo visibilidad de su error concreto (log, mensaje de red, o de la app), así que esto es una lista de sospechosos ordenada por probabilidad, no un diagnóstico confirmado:

1. **IP vencida**: tu IP de red cambia por DHCP entre reconexiones de Wi-Fi (ya pasó en esta sesión: de `.230` a `.130`). Si Dayana tiene guardada una IP vieja, va a fallar aunque todo lo demás esté bien. Confirmá tu IP actual con `ipconfig` y pasásela de nuevo.
2. **WildFly no está corriendo del lado tuyo en el momento en que ella prueba** — la conexión depende de que tu backend esté levantado y desplegado en ese instante.
3. **CORS solo aplica si la llamada sale del browser (JavaScript/AJAX)**. Si JSF/PrimeFaces del lado de Dayana llama a tu API desde el **backend Java de su propia app** (server-to-server), CORS no tiene nada que ver — ahí el problema sería de red pura (firewall, IP, puerto), no de configuración HTTP. Preguntale a Dayana si el error es un error de CORS en la consola del navegador, o una excepción de conexión del lado de su servidor — son causas completamente distintas.
4. **Firewall de Windows de tu lado**: la primera conexión entrante al puerto 8080 desde otra máquina puede quedar bloqueada. Revisá reglas de entrada.
5. **Distinta red/Wi-Fi**: confirmar que las dos laptops estén en el mismo router.
6. **Mismatch de contexto/ruta**: confirmar que ella esté usando exactamente `/estadisticas/api/...` y no `/api/...` a secas ni `/estadisticas/...` sin el `/api`.

**Para avanzar de verdad**, lo más útil sería el mensaje de error exacto que le tira a Dayana (texto de la excepción, código de status HTTP, o captura de la consola del navegador/log del servidor). Con eso puedo decirte cuál de estos 6 puntos es.
