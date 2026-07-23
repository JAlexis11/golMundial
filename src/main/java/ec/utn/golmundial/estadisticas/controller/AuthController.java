package ec.utn.golmundial.estadisticas.controller;

import ec.utn.golmundial.estadisticas.dto.LoginRequest;
import ec.utn.golmundial.estadisticas.dto.LoginResponse;
import ec.utn.golmundial.estadisticas.dto.RegistroRequest;
import ec.utn.golmundial.estadisticas.entity.Usuario;
import ec.utn.golmundial.estadisticas.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    private AuthService authService;

    @POST
    @Path("/registro")
    public Response registrar(RegistroRequest request) {
        Usuario usuario = authService.registrar(request.getEmail(), request.getUsername(),
                request.getFullName(), request.getPassword());
        return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                        "idUsuario", usuario.getIdUsuario(),
                        "email", usuario.getEmail(),
                        "username", usuario.getUsername(),
                        "fullName", usuario.getFullName(),
                        "rol", usuario.getRol().name()))
                .build();
    }

    @POST
    @Path("/login")
    public LoginResponse login(LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring("Bearer ".length()));
        }
        return Response.ok(Map.of("mensaje", "Sesión cerrada.")).build();
    }
}
