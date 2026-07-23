# Guía de integración — Backend de Estadísticas (para Fer y Dayana)

**Para qué sirve este documento:** esto es la referencia rápida de lo que necesitan Fer (Frontend Administrativo, Blazor) y Dayana (Frontend Público, JSF) para consumir la API del Backend de Estadísticas. El detalle completo de arquitectura/modelo de datos está en `INFORME_MAESTRO_ESTADISTICAS.md`; este documento es solo "qué llamar y con qué".

---

## URL base

```
http://<IP-de-esta-laptop>:8080/estadisticas/api/...
```

- **`<IP-de-esta-laptop>`**: la IP real de la máquina donde corre WildFly, en la red donde estén conectados todos el día de la integración (no `localhost`, salvo que Fer/Dayana estén probando en la misma máquina). Sacala con `ipconfig` (Windows) — el adaptador que tenga **puerta de enlace predeterminada** configurada, no un adaptador virtual tipo VirtualBox/Hyper-V.
- **`:8080`**: puerto por defecto de WildFly (confirmar si se cambió en el `standalone.xml` del servidor real).
- **`/estadisticas`**: context root del WAR — viene del `finalName` en `pom.xml` (`estadisticas.war`). **Esto es fácil de olvidar**: sin este segmento, cualquier llamada da 404.
- **`/api/...`**: el resto de las rutas de este documento (`/api/selecciones`, `/api/auth/login`, etc.) van después de eso.

Ejemplo completo real: `http://192.168.1.50:8080/estadisticas/api/grupos`.

Si Fer o Dayana están en una red distinta al servidor (WiFi con aislamiento de clientes, por ejemplo), ninguna llamada va a llegar aunque el código esté bien — hay que estar todos en la misma red sin aislamiento (ver `DESPLIEGUE_LINUX.md` para más detalle de red).

---

## 0. CORS

**Ya está configurado** (`CorsFilter`, `Access-Control-Allow-Origin: *`) — Blazor y JSF pueden llamar a este backend desde su propio origen/puerto sin que el navegador los bloquee. Incluye manejo del preflight `OPTIONS` (no requiere token, a diferencia del resto de las rutas protegidas).

---

## 1. Formato de errores (uniforme en todos los endpoints)

Cualquier error devuelve JSON con esta forma, nunca un stack trace crudo:

```json
{ "mensaje": "texto legible del error" }
```

Códigos usados: `400` (dato inválido), `401` (falta o venció la sesión), `403` (sesión válida pero sin permiso de administrador), `404` (recurso no existe), `500` (error interno).

---

## 2. Autenticación — pública, no necesita rol

### `POST /api/auth/registro`
```json
// Request
{ "email": "usuario@mail.com", "username": "usuario1", "fullName": "Nombre Apellido", "password": "123456" }
```
```json
// 201 Created
{ "idUsuario": 5, "email": "usuario@mail.com", "username": "usuario1", "fullName": "Nombre Apellido", "rol": "USUARIO_REGISTRADO" }
```
- `password` debe tener al menos 6 caracteres. `email`, `username` y `fullName` son todos requeridos.
- Este endpoint **ya dispara automáticamente** la creación de la billetera en UTNGolCoin (10 monedas de bienvenida) — no hace falta que el frontend llame aparte a UTNGolCoin para esto.
- 400 si el email o el username ya están registrados, o si falta algún campo.

### `POST /api/auth/login`
```json
// Request
{ "email": "usuario@mail.com", "password": "123456" }
```
```json
// 200 OK
{
  "token": "3f2e4b1a-....-uuid", "rol": "USUARIO_REGISTRADO", "expiracion": "2026-07-22T18:30:00",
  "email": "usuario@mail.com", "username": "usuario1", "fullName": "Nombre Apellido"
}
```
- Guardar el `token`: hay que mandarlo en el header `Authorization: Bearer <token>` en toda llamada que lo requiera (ver sección 4).
- La sesión expira a las 2 horas de creada.
- 401 si el email o la contraseña no matchean.

### `POST /api/auth/logout`
- Header: `Authorization: Bearer <token>`
- 200: `{ "mensaje": "Sesión cerrada." }` — invalida el token en el momento (no hace falta esperar a que expire).

---

## 3. Lectura pública — sin token, para invitados y usuarios logueados por igual

Estos endpoints los puede llamar cualquiera, logueado o no (acceso de invitado de solo lectura):

### `GET /api/selecciones`
```json
[{ "idSeleccion": 1, "nombre": "México", "confederacion": "CONCACAF", "grupo": "A" }]
```

### `GET /api/selecciones/{id}`
Mismo shape que arriba, un solo objeto. `404` si no existe.

### `GET /api/selecciones/grupo/{letra}`
Ej. `GET /api/selecciones/grupo/A` → lista de selecciones de ese grupo.

### `GET /api/grupos`
```json
[{
  "idGrupo": 1, "nombre": "A",
  "selecciones": [{ "idSeleccion": 1, "nombre": "México", "confederacion": "CONCACAF" }]
}]
```

### `GET /api/sedes`
```json
[{ "idSede": 1, "ciudad": "Ciudad de México", "estadio": "Estadio Azteca" }]
```

### `GET /api/partidos`
Calendario completo:
```json
[{
  "idPartido": 1, "seleccionLocal": "México", "seleccionVisitante": "Sudáfrica",
  "sede": "Ciudad de México", "fecha": "2026-06-11T15:00:00", "fase": "Grupo"
}]
```
`fase` es uno de: `Grupo`, `Octavos`, `Cuartos`, `Semifinal`, `Final` (confirmar contra `FaseEnum.java` antes de asumirlo — este enum cambió de nombres varias veces durante el desarrollo).

**Ojo con `fecha`**: viene como `LocalDateTime` sin zona horaria (ej. `"2026-06-11T15:00:00"`, sin `Z` al final). No asuman UTC ni ninguna zona en particular hasta que esto se confirme/migre.

### `GET /api/partidos/seleccion/{idSeleccion}/detallado`
Mismo shape que el calendario, filtrado a los partidos de esa selección.

### `GET /api/estadisticas`
```json
[{
  "idEstadistica": 1, "idSeleccion": 3, "nombreSeleccion": "Brasil",
  "partidosJugados": 3, "ganados": 2, "empatados": 1, "perdidos": 0,
  "golesFavor": 6, "golesContra": 2, "puntos": 7
}]
```
Ordenado por puntos descendente, luego diferencia de gol descendente. **Es una lista global de todas las selecciones, no separada por grupo** — si necesitan la tabla de un grupo en particular, hoy hay que filtrar del lado del frontend por el campo `grupo` que trae `GET /api/selecciones`.

---

## 4. Escritura — requiere rol ADMINISTRADOR (para el panel de Fer)

Todo esto necesita el header `Authorization: Bearer <token>` de un login con rol `ADMINISTRADOR`. Sin el header: `401`. Con token pero rol `USUARIO_REGISTRADO`/`INVITADO`: `403`.

### `POST /api/selecciones`
```json
{ "nombre": "Brasil", "confederacion": "CONMEBOL", "grupo": { "idGrupo": 3 } }
```

### `PUT /api/selecciones`
Mismo shape, incluyendo `idSeleccion`.

### `DELETE /api/selecciones/{id}`

### `POST /api/partidos`
```json
{
  "seleccionLocal": { "idSeleccion": 1 }, "seleccionVisitante": { "idSeleccion": 2 },
  "sede": { "idSede": 1 }, "fecha": "2026-06-11T15:00:00", "fase": "Grupo"
}
```
- `400` si falta selección local/visitante/sede, si son la misma selección, o si falta la fecha.

### `PUT /api/partidos`
Mismo shape, incluyendo `idPartido`.

### `DELETE /api/partidos/{id}`

### `POST /api/resultados`
```json
{ "partido": { "idPartido": 5 }, "golesLocal": 2, "golesVisitante": 1 }
```
- Recalcula automáticamente las estadísticas de ambas selecciones y notifica a UTNGolCoin para que liquide las apuestas de ese partido — no hay que hacer nada más desde el frontend para eso.
- `400` si el partido no existe, ya tiene resultado, o los goles son negativos.

### `GET /api/usuarios` *(también requiere ADMINISTRADOR, a diferencia de las lecturas de la sección 3)*
```json
[{ "idUsuario": 1, "email": "admin@utn.edu", "username": "admin1", "fullName": "Admin Uno", "rol": "ADMINISTRADOR", "fechaRegistro": "2026-07-01T10:00:00" }]
```

### `PUT /api/usuarios/{id}/rol`
```json
{ "rol": "ADMINISTRADOR" }
```
Valores válidos: `ADMINISTRADOR`, `USUARIO_REGISTRADO`, `INVITADO`.

---

## 5. Lo que todavía NO existe (para no asumirlo al diseñar pantallas)

- No hay endpoint de "tabla de posiciones del grupo X" ya armado con desempate — hoy `GET /api/estadisticas` es global.
- No hay endpoint que muestre el log de auditoría (existe la tabla, no el endpoint de consulta).

---

## 6. Resumen — quién llama qué

| Frontend | Endpoints que usa |
|---|---|
| **Dayana (público)** | `POST /auth/registro`, `POST /auth/login`, `POST /auth/logout`, y toda la sección 3 (lectura pública) |
| **Fer (admin)** | `POST /auth/login` (como ADMINISTRADOR), toda la sección 3, y toda la sección 4 (CRUD selecciones, registrar resultado, gestión de usuarios/roles) |
