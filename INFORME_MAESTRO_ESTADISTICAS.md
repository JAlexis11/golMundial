# INFORME MAESTRO - Backend de Estadísticas (UTN GolMundial 2026)

**Documento autosuficiente.** Si estás leyendo esto sin más contexto del proyecto (por ejemplo, alguien le pasó este archivo a una IA para construir su frontend o su backend de UTNGolCoin), esto alcanza para entender este backend completo: su arquitectura, su modelo de datos, sus reglas de negocio, su catálogo de endpoints, su seguridad, y cómo se conecta con el resto del sistema. Todo lo que dice este documento fue verificado contra el código real del repositorio.

---

## 1. Resumen ejecutivo

**Backend de Estadísticas** es uno de los 4 componentes del proyecto integrador universitario **UTN GolMundial 2026** (seguimiento del Mundial 2026 con una dinámica de apuestas en moneda ficticia).

| Componente | Responsable | Tecnología | Rol |
|---|---|---|---|
| **Backend de Estadísticas (este)** | Alexis | Jakarta EE (JAX-RS + JPA/Hibernate), WildFly, Linux | Dueño de usuarios, roles, selecciones, grupos, sedes, calendario, resultados oficiales, posiciones y estadísticas |
| Backend UTNGolCoin | Jonathan | ASP.NET Core Web API (.NET 9), MariaDB | Moneda virtual, billeteras, apuestas, liquidación de premios, ranking, reportes |
| Frontend Administrativo | Fer | Blazor / ASP.NET Core MVC (C#) | Panel admin: gestión de partidos/usuarios, registro de resultados, reportes |
| Frontend Público | Dayana | JSF + PrimeFaces | Registro/login, calendario, posiciones, predicciones, billetera, ranking |

**Este backend es la "dependencia raíz" del proyecto**: es dueño de los usuarios (todos los demás componentes confían en el `usuarioId` que este backend genera) y dispara la notificación que hace que UTNGolCoin liquide las apuestas cuando termina un partido.

**Comunicación con UTNGolCoin: unidireccional, la inicia siempre este backend.** UTNGolCoin nunca llama a Estadísticas. Ver sección 7.

**Estado de conexión de los consumidores (al 2026-07-23)**: el **Frontend Administrativo** (Fer, Blazor) ya se conecta y funciona bien contra este backend. El **Frontend Público** (Dayana, JSF+PrimeFaces) **todavía no logra conectarse** — diagnóstico y contrato exacto para resolverlo en la sección 10.2.

---

## 2. Arquitectura

### 2.1 Stack técnico exacto

- **Framework:** Jakarta EE 10 (JAX-RS para REST, JPA/Hibernate para persistencia, CDI para inyección de dependencias, EJB `@Stateless` para la capa de servicio/repositorio).
- **Servidor de aplicaciones:** WildFly (perfil **full**, no `standalone-web.xml` — se necesitan los subsistemas de EJB/JTA además de JAX-RS).
- **Base de datos:** **PostgreSQL**, propia de este backend (sin acceso cruzado a la base de UTNGolCoin — RNF06).
- **ORM:** Hibernate vía JPA estándar (`persistence.xml`), con `hibernate.hbm2ddl.auto=update`: el esquema se crea/actualiza solo al desplegar, no hace falta un script DDL aparte.
- **Empaquetado:** Maven, `packaging=war` (`pom.xml`), Java 17. `jakarta.jakartaee-api` en scope `provided` (lo pone WildFly en runtime).
- **Hash de contraseñas:** `jbcrypt` 0.4 (BCrypt) — única dependencia de terceros agregada aparte del driver de Postgres.
- **Cliente HTTP saliente (hacia UTNGolCoin):** `java.net.http.HttpClient`, nativo del JDK — no se agregó ninguna librería extra para esto.

### 2.2 Estructura de carpetas real

Todo el código vive en `src/main/java/ec/utn/golmundial/estadisticas/`:

- **`entity/`** — 10 clases `@Entity`: `Seleccion`, `Grupo`, `Sede`, `Partido`, `Resultado`, `EstadisticaSeleccion`, `Auditoria`, `Usuario`, `Sesion`, más los enums `FaseEnum` y `RolEnum`.
- **`repository/`** — una clase `@Stateless` por entidad principal, con `EntityManager` inyectado y JPQL escrito a mano (sin Spring Data ni generación automática de queries).
- **`service/`** — la lógica de negocio: `SeleccionService`, `PartidoService`, `ResultadoService`, `EstadisticaService`, `GrupoService`, `AuthService`, `UsuarioService`, `AuditoriaService`.
- **`controller/`** — los recursos JAX-RS (`@Path`): `SeleccionController`, `GrupoController`, `SedeController`, `PartidoController`, `ResultadoController`, `EstadisticaController`, `AuthController`, `UsuarioController`, más `RestApplication` (`@ApplicationPath("/api")`).
- **`dto/`** — objetos planos de transporte (nunca se serializan las entidades JPA directamente en las rutas de lectura masiva, para evitar problemas de fetch lazy y de referencias circulares — ver sección 3).
- **`security/`** — `AuthFilter` (filtro JAX-RS global de autenticación/autorización) y `UsuarioActual` (bean `@RequestScoped` que lleva el usuario autenticado de la request actual).
- **`integration/`** — `UtnGolCoinClient`, el único punto del código que hace llamadas HTTP salientes.
- **`exception/`** — `GlobalExceptionMapper`, traduce cualquier excepción a la respuesta JSON uniforme `{"mensaje": "..."}`.
- **`src/main/resources/META-INF/persistence.xml`** — configuración de la unidad de persistencia `estadisticasPU`.
- **`src/main/resources/seed/seed_mundial2026.sql`** — carga inicial del torneo (ver sección 8).

### 2.3 Por qué las listas devuelven DTOs y no entidades

Las entidades tienen relaciones bidireccionales (`Grupo`↔`Seleccion`↔`Partido`) con colecciones `@OneToMany` en `FetchType.LAZY` (el valor por defecto de JPA). Si un endpoint devolviera la entidad directamente:

1. **Crash por fetch lazy fuera de transacción**: los `@Stateless` EJB cierran su transacción/contexto de persistencia al retornar; JAX-RS serializa la respuesta *después* de eso, así que tocar una colección lazy en ese momento tira `LazyInitializationException`.
2. **Recursión infinita**: `Grupo.selecciones` → cada `Seleccion.grupo` → el mismo `Grupo` → bucle sin fin al serializar.

Por eso, **todo endpoint de lectura de listas devuelve un DTO** (`SeleccionDTO`, `GrupoDTO`, `PartidoDTO`, `EstadisticaDTO`), construido con una consulta JPQL de proyección (`SELECT new paquete.DTO(...) FROM ...`) que solo trae columnas escalares — nunca entidades completas. La única excepción es `GET /api/sedes`, que sí devuelve la entidad `Sede` cruda, pero es segura porque `Sede` no tiene ninguna otra relación expuesta al serializador (su única colección, `partidos`, está anotada `@JsonbTransient` y por lo tanto JSON-B nunca la toca).

**El proveedor JSON de este despliegue es JSON-B (Yasson)**, el estándar de Jakarta EE — se confirma por el uso de `jakarta.json.bind.annotation.JsonbTransient` en `Sede.java`.

---

## 3. Modelo de datos

### Selección (`seleccion`)
| Campo | Tipo | Notas |
|---|---|---|
| `idSeleccion` | `Integer` (PK, IDENTITY) | columna `id_seleccion` |
| `nombre` | `String` | not null |
| `confederacion` | `String` | not null (ej. `CONMEBOL`, `UEFA`, `CAF`, `AFC`, `CONCACAF`, `OFC`) |
| `grupo` | `@ManyToOne Grupo` | columna `id_grupo` |
| `partidosLocal` / `partidosVisitante` | `@OneToMany` (lazy) | nunca se serializan directamente |

### Grupo (`grupo`)
`idGrupo` (PK), `nombre` (1 carácter, "A".."L"), `selecciones` (`@OneToMany`, lazy).

### Sede (`sede`)
`idSede` (PK), `ciudad`, `estadio`, `partidos` (`@OneToMany(mappedBy="sede")`, `@JsonbTransient`).

### Partido (`partido`)
| Campo | Tipo | Notas |
|---|---|---|
| `idPartido` | `Integer` (PK) | columna `id_partido` |
| `seleccionLocal` / `seleccionVisitante` | `@ManyToOne Seleccion` (LAZY) | columnas `id_seleccion_local` / `id_seleccion_visitante` |
| `sede` | `@ManyToOne Sede` (LAZY) | columna `id_sede` |
| `fecha` | `LocalDateTime` | **sin zona horaria** — ver limitación en sección 10 |
| `fase` | `FaseEnum` (`EnumType.STRING`) | columna `fase` |

`FaseEnum`: `Grupo`, `Octavos`, `Cuartos`, `Semifinal`, `Final`. **Nota**: este enum ya cambió de nombres más de una vez durante el desarrollo (antes tuvo valores como `FASE_GRUPOS`/`DIECISEISAVOS`/`TERCER_PUESTO`) — antes de confiar en esta lista, confirmar contra `entity/FaseEnum.java` directamente, porque `@Enumerated(EnumType.STRING)` compara contra el nombre exacto de la constante.

### Resultado (`resultado`)
`idResultado` (PK), `partido` (`@OneToOne`, columna `idPartido`), `golesLocal` (int), `golesVisitante` (int). Un partido solo puede tener un `Resultado` (se valida en `ResultadoService`, no hay `unique` a nivel de columna).

### EstadisticaSeleccion (`estadistica_seleccion`)
`idEstadistica` (PK), `seleccion` (`@ManyToOne`), `partidosJugados`, `ganados`, `empatados`, `perdidos`, `golesFavor`, `golesContra`, `puntos` — todos `int`. Una fila por selección, recalculada acumulativamente cada vez que se registra un resultado (ver sección 4).

### Usuario (`usuario`)
| Campo | Tipo | Notas |
|---|---|---|
| `idUsuario` | `Integer` (PK, IDENTITY) | **este es el `usuarioId` que consume UTNGolCoin** |
| `email` | `String` | `unique=true` |
| `passwordHash` | `String` | hash BCrypt (`org.mindrot.jbcrypt`), nunca texto plano |
| `rol` | `RolEnum` (`EnumType.STRING`) | `ADMINISTRADOR`, `USUARIO_REGISTRADO`, `INVITADO` |
| `fechaRegistro` | `LocalDateTime` | |

### Sesion (`sesion`)
`token` (PK, `String`, un UUID v4 usado directamente como clave), `usuario` (`@ManyToOne`), `fechaCreacion`, `fechaExpiracion` (2 horas después de creada). Borrar la fila = logout inmediato.

### Auditoria (`auditoria`)
`idAuditoria` (PK), `tablaAfectada` (String, ej. `"seleccion"`, `"resultado"`, `"usuario"`), `idRegistro` (Integer, id del registro afectado), `accion` (String, ej. `"CREAR"`, `"ACTUALIZAR"`, `"ELIMINAR"`, `"REGISTRO"`, `"CAMBIAR_ROL:ADMINISTRADOR"`), `fecha`, y **`usuarioActor`** (`@ManyToOne Usuario`) — quién ejecutó la acción. Es un log de auditoría genérico y polimórfico: no tiene FK real hacia la tabla que describe (`tablaAfectada`/`idRegistro` son solo texto + número), pero sí tiene FK real hacia `Usuario` para el actor.

---

## 4. Reglas de negocio

1. **Recalculo de estadísticas al registrar un resultado** (`EstadisticaService.actualizarEstadisticas`): por cada resultado nuevo, a la selección local y a la visitante se les suma 1 partido jugado, se les suman los goles a favor/en contra correspondientes, y según quién ganó: +3 puntos y +1 a `ganados` para el ganador, +1 punto y +1 a `empatados` para ambos si empatan, +1 a `perdidos` para el perdedor. Usa `em.getReference(Seleccion.class, id)` para asociar la selección al crear una fila de estadística nueva, evitando el anti-patrón de instanciar una entidad "a mano" con solo el id seteado (que dispararía `TransientObjectException` de Hibernate).

2. **Un resultado por partido, sin duplicados** (`ResultadoService.registrarResultado`): antes de crear, busca si ya existe un `Resultado` para ese `idPartido`; si existe, rechaza con `IllegalArgumentException` (→ 400).

3. **Validación mínima de resultado**: `partido` requerido con `idPartido` no nulo, goles no negativos. No hay validación de que la fecha del partido ya haya pasado (podría registrarse un resultado "del futuro" sin que el sistema lo impida hoy).

4. **Reasignación a la entidad gestionada antes de persistir**: el `Resultado` que llega en el body del `POST` trae un objeto `Partido` "suelto" (deserializado del JSON, no gestionado por JPA). Antes de `persist`, `ResultadoService` lo reemplaza por el `Partido` real obtenido con `partidoService.buscarPorId(...)` — si no se hiciera este paso, Hibernate lanzaría `TransientObjectException` al hacer flush de la relación `@OneToOne`.

5. **Notificación a UTNGolCoin al registrar resultado (RF11→RF12)**: después de persistir el resultado y recalcular estadísticas, se determina el resultado textual (`LOCAL`/`EMPATE`/`VISITANTE` según los goles) y se llama a `UtnGolCoinClient.notificarLiquidacion(idPartido, resultado)`. Ver contrato exacto en sección 7.

6. **Registro de usuario y billetera (RF01)**: al registrarse (`AuthService.registrar`), se valida email no vacío, contraseña de al menos 6 caracteres, y que el email no esté ya registrado. Se hashea la contraseña con BCrypt, se asigna rol `USUARIO_REGISTRADO` por defecto, y **solo si el rol es `USUARIO_REGISTRADO`** se dispara la creación de billetera en UTNGolCoin (`UtnGolCoinClient.crearBilletera`) — un `INVITADO` no apuesta, no necesita billetera.

7. **Login / sesión** (`AuthService.login`): valida email + `BCrypt.checkpw`, crea una `Sesion` con token UUID aleatorio y expiración a 2 horas, la persiste, devuelve el token + rol + fecha de expiración. Si las credenciales no matchean, lanza `NotAuthorizedException` (→ 401).

8. **Logout**: borra la fila de `Sesion` correspondiente al token — invalidación inmediata y real (no es solo "olvidar el token del lado del cliente").

9. **Auditoría con actor**: cada acción administrativa (`crear`/`actualizar`/`eliminar` selección, registrar resultado, cambiar rol, y el propio registro de usuario) llama a `AuditoriaService.registrar(tabla, idRegistro, accion, idUsuarioActor)`. El `idUsuarioActor` sale de `UsuarioActual`, un bean `@RequestScoped` que `AuthFilter` completa con el usuario resuelto del token `Bearer` en cada request protegida (para el auto-registro de un usuario nuevo, el actor es el propio usuario recién creado).

10. **Degradación controlada (RNF05)**: toda llamada de `UtnGolCoinClient` (liquidación y creación de billetera) está en un `try/catch` con timeout de 3 segundos; cualquier fallo (timeout, conexión rechazada, error HTTP) se loguea con `java.util.logging` y se ignora — nunca se propaga hacia el llamador, así que un resultado registrado o un usuario registrado en este backend **nunca se revierte** porque UTNGolCoin esté caído.

---

## 5. Seguridad (RNF04, RF25, RF26)

- **Hash de contraseñas**: BCrypt (`org.mindrot.jbcrypt`, `BCrypt.hashpw`/`BCrypt.checkpw`), con salt aleatorio por `BCrypt.gensalt()`. Nunca se guarda ni se loguea la contraseña en texto plano.
- **Sesión**: token opaco (UUID v4) guardado en la tabla `sesion` de la propia base, con expiración de 2 horas. Se eligió este esquema en vez de JWT por ser más simple de explicar y de invalidar (un logout es un `DELETE` real de la fila; un JWT autocontenido no se puede "revocar" sin una lista de bloqueo aparte).
- **Filtro global (`AuthFilter`, `@Provider` `ContainerRequestFilter`, `@Priority(Priorities.AUTHENTICATION)`)**, aplicado a **todas** las rutas:
  - `/api/auth/*` (registro, login, logout) → siempre público, ninguna de las dos direcciones necesita token.
  - Cualquier `GET` **salvo** `/api/usuarios` → público, sin autenticación (RF26: acceso de invitado de solo lectura al calendario, grupos, posiciones, estadísticas, selecciones, sedes, partidos).
  - `GET /api/usuarios` y **cualquier `POST`/`PUT`/`DELETE`** fuera de `/auth` → requieren header `Authorization: Bearer <token>` de una sesión válida y no expirada, **y** que el usuario tenga rol `ADMINISTRADOR` (RF25). Sin eso: 401 (falta token o sesión inválida/expirada) o 403 (token válido pero rol insuficiente).
  - Esto protege automáticamente, sin tocar cada controlador uno por uno, el CRUD de `Seleccion` y el registro de `Resultado`.
- **`USUARIO_REGISTRADO` vs. invitado anónimo, en este backend, son funcionalmente iguales** (ambos de solo lectura) — es correcto, no un descuido: las acciones de "usuario" propiamente dichas (apostar, ver billetera) viven en UTNGolCoin, no acá. La diferenciación de rol que sí importa en este backend es `ADMINISTRADOR` vs. todo el resto.
- **`Auditoria`** deja constancia de qué se hizo, sobre qué tabla/id, cuándo, y quién (usuario actor) lo hizo — cubre RF24.

---

## 6. Catálogo completo de endpoints

Todas las rutas cuelgan de `/api` (`RestApplication`, `@ApplicationPath("/api")`). Formato de error uniforme: `{"mensaje": "texto"}` (`GlobalExceptionMapper`).

### AuthController — `/api/auth` (siempre público)

#### `POST /api/auth/registro`
- Body: `{"email": "user@mail.com", "password": "123456"}`
- 201: `{"idUsuario": 5, "email": "user@mail.com", "rol": "USUARIO_REGISTRADO"}`
- 400: email requerido / contraseña muy corta / email ya registrado.
- Efecto secundario: dispara `POST /api/billeteras` en UTNGolCoin con ese `idUsuario` (no bloqueante).

#### `POST /api/auth/login`
- Body: `{"email": "...", "password": "..."}`
- 200: `{"token": "3f2e...uuid", "rol": "USUARIO_REGISTRADO", "expiracion": "2026-07-22T18:30:00"}`
- 401: `{"mensaje": "Email o contraseña inválidos."}`

#### `POST /api/auth/logout`
- Header: `Authorization: Bearer <token>`
- 200: `{"mensaje": "Sesión cerrada."}`

### UsuarioController — `/api/usuarios` (siempre requiere rol ADMINISTRADOR, incluido el GET)

#### `GET /api/usuarios`
- 200: `[{"idUsuario":1,"email":"...","rol":"ADMINISTRADOR","fechaRegistro":"..."}]`

#### `PUT /api/usuarios/{id}/rol`
- Body: `{"rol": "ADMINISTRADOR"}`
- 400: `{"mensaje": "El rol debe ser ADMINISTRADOR, USUARIO_REGISTRADO o INVITADO."}`

### SeleccionController — `/api/selecciones`

- `GET /api/selecciones` (público):
```json
[{"idSeleccion":1,"nombre":"México","confederacion":"CONCACAF","grupo":"A"}]
```
- `GET /api/selecciones/{id}` (público) → mismo shape (un solo objeto, no array), o 404 `{"mensaje":"La selección N no existe."}`
- `GET /api/selecciones/grupo/{grupo}` (público, ej. `/grupo/A`) → array con el mismo shape, filtrado
- `POST /api/selecciones` (**ADMIN**) — body: entidad `Seleccion` cruda (incluye `{"grupo":{"idGrupo":N}}`)
- `PUT /api/selecciones` (**ADMIN**) — mismo body; requiere `idSeleccion` y que exista (404 si no)
- `DELETE /api/selecciones/{id}` (**ADMIN**) — 404 si el id no existe

### GrupoController — `/api/grupos` (público)

#### `GET /api/grupos`
```json
[{"idGrupo":1,"nombre":"A","selecciones":[{"idSeleccion":1,"nombre":"México","confederacion":"CONCACAF"}, ...]}]
```
Compuesto en `GrupoService.listar()`: trae los grupos con `GrupoRepository` (proyección a `GrupoDTO`) y, por cada uno, completa `selecciones` con `SeleccionRepository.listarResumenPorGrupo` (proyección a `SeleccionResumenDTO`). Dos consultas JPQL de proyección encadenadas — nunca se toca la colección lazy `Grupo.selecciones` de la entidad.

### SedeController — `/api/sedes` (público)
`GET /api/sedes` → `[{"idSede":1,"ciudad":"Ciudad de México","estadio":"Estadio Azteca"}]`

### PartidoController — `/api/partidos`

- `GET /api/partidos` (público) — calendario completo:
```json
[
  {
    "idPartido": 1,
    "seleccionLocal": "México",
    "seleccionVisitante": "Sudáfrica",
    "sede": "Ciudad de México",
    "fecha": "2026-07-11T15:00:00",
    "fase": "Grupo"
  }
]
```
Ojo: `seleccionLocal`/`seleccionVisitante` son **strings con el nombre** (no objetos, no ids), y `sede` es el **string `ciudad`** (no el estadio, no un objeto) — vienen así porque `PartidoDTO` los proyecta directo desde el JOIN JPQL (`PartidoRepository.listar`), no arma un objeto anidado.
- `GET /api/partidos/seleccion/{idSeleccion}/detallado` (público) — mismo shape, filtrado por selección.
- `POST /api/partidos` (**ADMIN**) — body: entidad `Partido` cruda (incluye `{"seleccionLocal":{"idSeleccion":N},"seleccionVisitante":{"idSeleccion":M},"sede":{"idSede":K},"fecha":"...","fase":"Grupo"}`).
- `PUT /api/partidos` (**ADMIN**) — mismo body, actualiza vía `em.merge`.
- `DELETE /api/partidos/{id}` (**ADMIN**).
- **CRUD completo** (`PartidoController`/`PartidoService`/`PartidoRepository`). `PartidoService.validar()` rechaza con 400 si: falta selección local o visitante, la local y la visitante son la misma selección, falta la sede, o falta la fecha. `crear`/`actualizar`/`eliminar` auditan igual que `Seleccion` (`AuditoriaService.registrar("partido", ...)`, actor resuelto vía `UsuarioActual`).
- `actualizar` valida que `idPartido` venga informado y que ese partido exista (404 `NotFoundException` si no) antes de hacer `em.merge` — evita que un `PUT` con id ausente/inexistente se convierta silenciosamente en un `INSERT` nuevo (mismo fix aplicado a `Seleccion`).
- `eliminar` valida primero que el partido **no** tenga ya un `Resultado` asociado (`ResultadoRepository.buscarPorPartido`); si lo tiene, 400 explícito en vez de dejar que la base rechace el `DELETE` por la FK de `Resultado.partido` (`@OneToOne` sin `cascade`/`orphanRemoval`) con una excepción sin traducir.

### ResultadoController — `/api/resultados` (**ADMIN**)

#### `POST /api/resultados`
- Body: `{"partido":{"idPartido":5},"golesLocal":2,"golesVisitante":1}`
- Efecto: valida, persiste, recalcula estadísticas de ambas selecciones, audita, notifica a UTNGolCoin.
- 400: partido requerido / goles negativos / partido inexistente / ya tiene resultado.

### EstadisticaController — `/api/estadisticas` (público)

#### `GET /api/estadisticas`
```json
[{"idEstadistica":1,"idSeleccion":3,"nombreSeleccion":"Brasil","partidosJugados":3,"ganados":2,"empatados":1,"perdidos":0,"golesFavor":6,"golesContra":2,"puntos":7}]
```
Ordenado por puntos descendente y luego diferencia de gol descendente. **Nota**: es una lista global de todas las selecciones, no agrupada por grupo — no hay hoy un endpoint "tabla de posiciones del grupo A" dedicado. **Ojo**: esta tabla arranca vacía (`[]`) — solo tiene filas para las selecciones que ya jugaron al menos un partido con `POST /api/resultados` registrado por un admin. En una base recién sembrada con el seed (sin resultados cargados), este endpoint devuelve `[]`, no es un bug.

---

## 7. Integración con UTNGolCoin

**Contrato verificado contra el informe maestro de Jonathan** (`INFORME_MAESTRO_UTNGOLCOIN.md` de este mismo repo).

- **Cliente**: `ec.utn.golmundial.estadisticas.integration.UtnGolCoinClient`, usa `java.net.http.HttpClient` con `connectTimeout` y `timeout` de request de 3 segundos.
- **URL base configurable**, nunca hardcodeada: variable de entorno `UTNGOLCOIN_BASE_URL` (default `http://localhost:5253` si no está seteada). El día de la integración en red, alcanza con setear `UTNGOLCOIN_BASE_URL=http://<IP-de-Jonathan>:5253` antes de levantar WildFly.

### Notificación de liquidación (RF11 → RF12)
- `POST {UTNGOLCOIN_BASE_URL}/api/utngolcoin/liquidacion`
- Body: `{"partidoId": <entero>, "resultado": "LOCAL"|"EMPATE"|"VISITANTE"}` — nombres de campo y valores en mayúsculas exactos, verificados contra el contrato de Jonathan.
- Se dispara desde `ResultadoService.registrarResultado`, justo después de auditar el resultado.
- Si UTNGolCoin no responde o tira error: se loguea con `java.util.logging.Logger` y se ignora. El resultado y las estadísticas ya guardados de este lado **no se revierten** (RNF05).

### Creación de billetera (RF01)
- `POST {UTNGOLCOIN_BASE_URL}/api/billeteras`
- Body: `{"usuarioId": <entero>}` — el `idUsuario` recién generado por `AuthService.registrar`.
- Se dispara solo para usuarios con rol `USUARIO_REGISTRADO`, con el mismo criterio de degradación controlada.
- **Decisión de coordinación tomada con Jonathan**: Opción B — este backend llama automáticamente al registrarse el usuario (en vez de que el frontend orqueste dos llamadas separadas).

---

## 8. Seed inicial del torneo (RF28)

`src/main/resources/seed/seed_mundial2026.sql` — carga:

- **16 sedes reales** (estadios confirmados del Mundial 2026 en USA/México/Canadá).
- **12 grupos reales** (A–L) con las **48 selecciones reales** confirmadas para el torneo, incluyendo los 6 clasificados de repechaje de marzo 2026 (Bosnia y Herzegovina, Suecia, Turquía, Chequia, RD Congo, Irak) ubicados en el grupo correcto según el sorteo oficial.
- **72 partidos de fase de grupos** (`fase = 'Grupo'`), round-robin completo por grupo (cada selección juega 3 partidos), mismos días (11 al 27) que la ventana real del torneo pero corridos a **julio 2026** (en vez de junio) a pedido, para que el calendario caiga en el mes en curso durante pruebas/demos. El propio `.sql` documenta el cambio y cómo revertirlo a la fecha real si hace falta.

**Importante — qué está verificado y qué no** (aclarado en el propio archivo): los grupos/selecciones/sedes están verificados contra fuentes reales (FIFA.com, ESPN, Olympics.com — links al final del `.sql`); la asignación exacta de fecha/hora/estadio de cada uno de los 72 partidos puntuales es una simulación estructuralmente correcta, no el fixture oficial verificado partido por partido.

**Alcance intencional**: no incluye los 32 partidos de eliminatorias — dependen de qué selecciones clasifican según resultados reales que todavía no existen el día que se corre el seed.

**Cómo se carga**: no hay ningún mecanismo automático — hay que ejecutar el `.sql` a mano (psql, pgAdmin, o el cliente que se use) contra la base ya creada por WildFly al desplegar (gracias a `hibernate.hbm2ddl.auto=update`).

---

## 9. Cómo levantar el proyecto

1. WildFly, perfil **full** (no alcanza `standalone-web.xml`: se necesitan JTA/EJB además de JAX-RS).
2. Datasource JNDI llamado exactamente `java:/jdbc/estadisticasDS`, apuntando a una base PostgreSQL propia (driver `org.postgresql:postgresql:42.7.1`, ya en el WAR).
3. `mvn clean package` → genera `target/estadisticas.war`.
4. Desplegar el WAR en `standalone/deployments/`. Al arrancar, Hibernate crea/actualiza el esquema solo (`hibernate.hbm2ddl.auto=update`) — no hace falta correr DDL a mano.
5. Cargar `src/main/resources/seed/seed_mundial2026.sql` contra esa misma base (a mano).
6. Si se va a usar la integración con UTNGolCoin fuera de `localhost`, setear la variable de entorno `UTNGOLCOIN_BASE_URL` antes de levantar WildFly.

## 10. Cómo conectarse en red

### 10.1 Con Jonathan (UTNGolCoin, día de la integración)

- Ambas laptops en el mismo router; obtener la IP local de cada una (`ipconfig` / `ip addr`).
- Este backend (WildFly) escucha según la configuración del propio servidor — confirmar puerto expuesto el día de la integración.
- Setear `UTNGOLCOIN_BASE_URL=http://<IP-de-Jonathan>:5253` de este lado para que `UtnGolCoinClient` le hable a la IP correcta.
- Revisar firewall: la primera conexión entre laptops puede requerir permitir el puerto.

### 10.2 Con el Frontend Público (Dayana, JSF + PrimeFaces)

**Base URL a usar desde su frontend**: `http://<IP-de-este-backend>:8080/estadisticas/api` (contexto `/estadisticas` porque `finalName=estadisticas` en `pom.xml` sin `web.xml` que lo pise; prefijo `/api` de `@ApplicationPath("/api")`).

**El Frontend Administrativo (Fer, Blazor) se conecta bien contra este mismo backend.** El problema de conexión reportado es específico del Frontend Público — no es que el backend en sí esté mal, así que antes de tocar código hay que descartar causas de entorno/red primero.

**Snapshot de diagnóstico en vivo — 2026-07-23** (esto es una foto de un momento puntual, no una garantía permanente; volver a chequear si pasa tiempo):
- IP de red del backend en ese momento: `192.168.0.230` (Wi-Fi). **Esta IP cambia por DHCP entre reconexiones** — en la misma sesión de trabajo pasó de `.230` a `.130` y volvió a `.230`. Siempre confirmar con `ipconfig` antes de asumir que es la de la última vez.
- Puerto 8080: **no estaba escuchando nada** (`Get-NetTCPConnection -LocalPort 8080` sin resultados).
- Proceso `java`: **ninguno corriendo** — WildFly no estaba levantado en ese momento.
- Regla de firewall entrante para el puerto 8080: **no se encontró ninguna configurada**.
- Conclusión de ese snapshot: mientras el backend no esté efectivamente desplegado y escuchando, **ninguna configuración del lado de Dayana va a poder conectar**, sin importar que la URL/contrato estén bien. Esto hay que resolverlo primero, antes de seguir buscando el problema en el contrato JSON o en el código del frontend.

**Causas más probables si la conexión sigue fallando incluso con el backend levantado y la IP confirmada** (de más a menos probable):
1. **CORS vs. llamada servidor-a-servidor** — distinción importante: `CorsFilter.java` ya deja `Access-Control-Allow-Origin: *` y `AuthFilter` deja pasar el preflight `OPTIONS` sin token, así que si el navegador de un usuario hace `fetch`/AJAX directo desde JavaScript, CORS no debería ser el problema. Pero si en cambio el **backend Java del propio Frontend Público** (típico en JSF: un managed bean llamando a la API por HTTP desde el servidor de Dayana, no desde el browser) es el que hace la llamada, CORS **no aplica en absoluto** — ahí cualquier error es de red pura (IP/puerto/firewall), no de configuración HTTP. Hay que confirmar de qué lado sale la llamada antes de seguir.
2. Firewall de Windows bloqueando la conexión entrante nueva al puerto 8080 (posible incluso con WildFly corriendo).
3. Las dos laptops no están en la misma red/router.
4. Ruta mal armada del lado de Dayana — falta el `/estadisticas` antes del `/api`, o falta el `/api` mismo.
5. `GET /api/estadisticas` devolviendo `[]` interpretado como "no conecta" cuando en realidad sí conectó pero no hay resultados cargados todavía (ver nota en la sección 6, `EstadisticaController`).

**Contrato exacto (endpoints, JSON, y qué requiere token) para que el Frontend Público consuma sin adivinar**: ver sección 6 completa — específicamente `AuthController` (login/registro/logout), y los `GET` públicos de `PartidoController`, `GrupoController`, `SeleccionController`, `SedeController`, `EstadisticaController`. Todos los `GET` documentados ahí son públicos salvo `GET /api/usuarios` (sección 5).

---

## 11. Checklist de requisitos cubiertos

| Requisito | Estado | Evidencia |
|---|---|---|
| RF01 (registro + billetera) | ✅ Cubierto | `AuthController.registrar` → `AuthService.registrar` → `UtnGolCoinClient.crearBilletera` |
| RF02/RF03 (login/logout) | ✅ Cubierto | `AuthController.login/logout`, `AuthService`, tabla `sesion` |
| RF04 (calendario completo) | ✅ Cubierto | `GET /api/partidos` vía `PartidoDTO`, seed de 72 partidos |
| RF05 (12 grupos) | ✅ Cubierto | `GET /api/grupos`, seed de 12 grupos reales |
| RF06 (recálculo automático de posiciones) | ⚠️ Parcial | Se recalcula `EstadisticaSeleccion` en cada resultado, pero no hay endpoint de tabla de posiciones agrupada por grupo con desempate |
| RF07 (estadísticas por selección) | ✅ Cubierto | `GET /api/estadisticas` (PJ/PG/PE/PP/GF/GC/Pts) |
| RF08 (detalle de partido: estado + marcador) | ⚠️ Parcial | Hay marcador (`Resultado`); no hay campo "estado" explícito, se infiere |
| RF09 (fases eliminatorias) | ⚠️ Parcial | `FaseEnum` completo; sin lógica de avance automático |
| RF10 (CRUD admin selecciones/partidos) | ✅ Cubierto | `Seleccion` y `Partido` con CRUD completo (`POST`/`PUT`/`DELETE`), validados y auditados |
| RF11/RF12 (registrar resultado → notificar UTNGolCoin) | ✅ Cubierto | `ResultadoService.registrarResultado` + `UtnGolCoinClient.notificarLiquidacion`, contrato verificado |
| RF23 (gestión de usuarios y roles) | ✅ Cubierto | `UsuarioController` (listar, cambiar rol) |
| RF24 (auditoría de acciones admin) | ✅ Cubierto | `Auditoria` + `usuarioActor`, wireado en Selección/Resultado/Usuario |
| RF25 (permisos diferenciados admin/usuario/invitado) | ✅ Cubierto | `AuthFilter` — escritura solo ADMIN |
| RF26 (acceso invitado de solo lectura) | ✅ Cubierto | Todo `GET` público salvo `/usuarios` |
| RF28 (seed inicial) | ✅ Cubierto (manual) | `seed/seed_mundial2026.sql`, 48 selecciones/12 grupos/16 sedes reales + 72 partidos |
| RNF03 (validación backend) | ⚠️ Parcial | Validaciones puntuales (goles, email, password); sin Bean Validation |
| RNF04 (passwords hasheadas) | ✅ Cubierto | BCrypt (`jbcrypt`), nunca texto plano |
| RNF05 (degradación controlada) | ✅ Cubierto | Try/catch + timeout 3s en `UtnGolCoinClient`, nunca propaga |
| RNF06 (base propia, sin acceso cruzado) | ✅ Cubierto | `persistence.xml` → datasource propio, dialecto PostgreSQL |
| RNF07 (Linux) | ⚠️ No verificado en runtime | Sin dependencias Windows-específicas; nunca desplegado contra un WildFly real en esta sesión |
| RNF08 (capas y documentado) | ✅ Cubierto | controller/service/repository/entity consistente en todos los módulos (incluido `Grupo`, que antes saltaba la capa de servicio) |
| RNF09 (Swagger/OpenAPI) | ❌ Faltante | Sin dependencia de OpenAPI en `pom.xml` |
| RNF10 (mensajes de error claros) | ✅ Cubierto | `GlobalExceptionMapper` → `{"mensaje": "..."}` uniforme |

---

## 12. Limitaciones honestas / mejoras futuras

- **Sin Swagger/OpenAPI (RNF09)** — pendiente agregar `smallrye-openapi` o equivalente.
- **`Partido.fecha` es `LocalDateTime` sin zona horaria** — si en algún momento se integra directamente con un sistema que espere ISO-8601 con `Z` (UTC explícito), hay que migrar a `OffsetDateTime`/`Instant`.
- ~~Falta `DELETE` de `Partido`~~ — resuelto: `PartidoController` ya expone `POST`/`PUT`/`DELETE` completos, validados (`PartidoService.validar`) y auditados igual que `Seleccion`.
- ~~`DELETE /api/partidos/{id}` no valida si el partido ya tiene `Resultado`~~ — resuelto: `PartidoService.eliminar` chequea `ResultadoRepository.buscarPorPartido` antes de borrar y devuelve 400 explícito si ya tiene resultado.
- ~~`PUT` de `Seleccion`/`Partido` sin `id` o con `id` inexistente insertaba una fila nueva en vez de fallar~~ — resuelto: `SeleccionService.actualizar` y `PartidoService.actualizar` ahora validan existencia antes de `merge` (404 si no existe).
- ~~`DELETE`/`PUT rol` sobre un id inexistente devolvía 200 sin hacer nada~~ — resuelto: `SeleccionRepository.eliminar`, `PartidoRepository.eliminar` y `UsuarioRepository.actualizarRol` ahora tiran `NotFoundException` (404) si el id no existe, en vez de no-opear en silencio.
- ~~`GlobalExceptionMapper` reenviaba el mensaje crudo de cualquier excepción, incluidas las 500 no controladas~~ — resuelto: para status ≥500 el cliente recibe un mensaje genérico y el detalle real queda logueado del lado del servidor (`java.util.logging`); los 4xx (validaciones de negocio) siguen devolviendo el mensaje específico, sin cambios.
- **Sin tabla de posiciones por grupo** con desempate por diferencia de gol dentro del grupo — hoy es una lista global (`GET /api/estadisticas`, sección 6).
- **Sin lógica de avance de fases eliminatorias** — el enum existe, el proceso de clasificación no.
- **Sin tests automatizados** — toda la verificación de esta sesión fue `mvn compile`/`mvn package`, nunca contra un WildFly + PostgreSQL corriendo de verdad.
- ~~Sin filtro CORS~~ — resuelto: `security/CorsFilter.java` (`Access-Control-Allow-Origin: *`), con `AuthFilter` dejando pasar el preflight `OPTIONS` sin exigir token.
- **`SeleccionController`/`ResultadoController` siguen aceptando entidades JPA crudas** como body de `POST`/`PUT` en vez de DTOs de request dedicados — funciona, pero acopla el contrato REST a la persistencia.
- **El seed no se auto-ejecuta** — hay que correrlo a mano contra la base ya creada.

---

## Nota de cierre

Este documento se generó leyendo el código fuente real de todo `src/main/java/ec/utn/golmundial/estadisticas/` y `src/main/resources/`, verificando cada afirmación contra el archivo correspondiente (no contra memoria ni suposiciones). Se validó además que el proyecto compila y empaqueta limpio (`mvn clean package`) en el estado descrito acá. El código fuente del repositorio es la fuente de verdad final — si cambia después, este documento hay que revisarlo de nuevo contra él.

**Última revisión (2026-07-23)**: se releyó el código completo contra este documento para detectar drift. Único hallazgo material: el CRUD de `Partido` (`POST`/`PUT`/`DELETE` en `PartidoController`, validación en `PartidoService`) ya estaba implementado en el código pero el documento todavía lo listaba como pendiente (secciones 6, 11 y 12 desactualizadas entre sí). Corregido.

**Revisión de bugs (2026-07-23, mismo día)**: pasada completa de code review sobre controllers/services/repositories buscando problemas de comportamiento (no solo de compilación). Se encontraron y corrigieron 4:
1. `DELETE` de selección/partido y `PUT /api/usuarios/{id}/rol` devolvían 200 sin hacer nada cuando el id no existía (silencioso).
2. `PUT` de selección/partido sin `id` o con `id` inexistente insertaba una fila nueva en vez de fallar (`em.merge` con id nulo/no encontrado).
3. `DELETE /api/partidos/{id}` no chequeaba si el partido ya tenía `Resultado` antes de borrar, arriesgando una FK violation sin traducir.
4. `GlobalExceptionMapper` reenviaba el mensaje crudo de cualquier excepción 500 (no controlada) al cliente — riesgo de fuga de detalle interno en endpoints públicos.

Los 4 quedaron corregidos y el proyecto compila limpio (`mvn clean package`) después de los cambios. También se corrió la ventana de fechas del seed de junio a julio 2026 (mismos días, mismos horarios) a pedido, para que el calendario caiga en el mes en curso durante pruebas — el propio `.sql` documenta cómo revertirlo.

**Actualización para integración con el Frontend Público (2026-07-23)**: se agregaron ejemplos JSON reales (no inventados — construidos desde los DTOs verificados en el código + datos reales del seed) a todos los `GET` públicos de la sección 6, y una sección nueva (10.2) con el diagnóstico exacto de por qué el Frontend Público (Dayana) no lograba conectarse todavía: en el snapshot tomado ese día, WildFly no estaba desplegado/corriendo (puerto 8080 sin nada escuchando, sin proceso `java`, sin regla de firewall para ese puerto) — es decir, el problema no era el contrato de la API sino que el backend no estaba levantado. Se documentó también la distinción entre CORS (solo aplica si la llamada sale del browser) y llamada servidor-a-servidor (típica en JSF), como primer punto a confirmar del lado de Dayana si la conexión sigue fallando una vez que el backend esté efectivamente arriba. El Frontend Administrativo (Fer) no tiene este problema — ya se conecta bien.
