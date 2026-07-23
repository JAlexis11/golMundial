package ec.utn.golmundial.estadisticas.security;

import ec.utn.golmundial.estadisticas.entity.RolEnum;
import ec.utn.golmundial.estadisticas.entity.Usuario;
import ec.utn.golmundial.estadisticas.service.AuthService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

/**
 * RF25/RF26: filtro global. /auth/* siempre público (login/registro/logout).
 * Lectura (GET) pública para invitados y usuarios, salvo /usuarios (listado de
 * cuentas, solo ADMIN). Cualquier escritura (POST/PUT/DELETE) fuera de /auth
 * requiere un token de sesión válido con rol ADMIN.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Inject
    private AuthService authService;

    @Inject
    private UsuarioActual usuarioActual;

    @Override
    public void filter(ContainerRequestContext ctx) {
        // Normalizado sin barra inicial: UriInfo.getPath() no garantiza el mismo
        // formato en todas las implementaciones JAX-RS, y un desfasaje acá haría
        // que ninguna ruta pública matchee (bloqueo total, incluido /auth/login).
        String path = ctx.getUriInfo().getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String method = ctx.getMethod();

        // Preflight de CORS: el navegador lo manda sin token antes de cualquier
        // POST/PUT/DELETE cross-origin o request con header Authorization. Si esto
        // no se deja pasar, el CORS queda roto igual aunque CorsFilter esté bien.
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return;
        }

        if (path.startsWith("auth/")) {
            return;
        }

        boolean esLecturaPublica = "GET".equalsIgnoreCase(method) && !path.startsWith("usuarios");
        if (esLecturaPublica) {
            return;
        }

        String header = ctx.getHeaderString("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            ctx.abortWith(error(401, "Debe iniciar sesión como administrador para esta operación."));
            return;
        }

        String token = header.substring("Bearer ".length());
        Usuario usuario = authService.validarToken(token);
        if (usuario == null) {
            ctx.abortWith(error(401, "Sesión inválida o expirada."));
            return;
        }
        if (usuario.getRol() != RolEnum.ADMINISTRADOR) {
            ctx.abortWith(error(403, "No tiene permisos de administrador para esta operación."));
            return;
        }

        ctx.setProperty("usuario", usuario);
        usuarioActual.setUsuario(usuario);
    }

    private Response error(int status, String mensaje) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("mensaje", mensaje))
                .build();
    }
}
