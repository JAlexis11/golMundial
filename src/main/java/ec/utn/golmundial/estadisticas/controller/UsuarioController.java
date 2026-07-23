package ec.utn.golmundial.estadisticas.controller;

import ec.utn.golmundial.estadisticas.dto.CambiarRolRequest;
import ec.utn.golmundial.estadisticas.dto.UsuarioDTO;
import ec.utn.golmundial.estadisticas.service.UsuarioService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioController {

    @Inject
    private UsuarioService usuarioService;

    @GET
    public List<UsuarioDTO> listar() {
        return usuarioService.listar();
    }

    @PUT
    @Path("/{id}/rol")
    public void cambiarRol(@PathParam("id") Integer id, CambiarRolRequest request) {
        usuarioService.cambiarRol(id, request.getRol());
    }
}
