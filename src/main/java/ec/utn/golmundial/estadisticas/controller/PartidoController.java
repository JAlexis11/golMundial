package ec.utn.golmundial.estadisticas.controller;

import ec.utn.golmundial.estadisticas.dto.PartidoDTO;
import ec.utn.golmundial.estadisticas.entity.Partido;
import ec.utn.golmundial.estadisticas.service.PartidoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/partidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PartidoController {

    @Inject
    private PartidoService partidoService;

    @GET
    public List<PartidoDTO> listar() {
        return partidoService.listar();
    }

    @GET
    @Path("/seleccion/{idSeleccion}/detallado")
    public List<PartidoDTO> listarDetalladoPorSeleccion(@PathParam("idSeleccion") Integer idSeleccion) {
        return partidoService.listarDetalladoPorSeleccion(idSeleccion);
    }

    // Requiere rol ADMINISTRADOR (protegido globalmente por AuthFilter para todo POST/PUT/DELETE).
    @POST
    public void crear(Partido partido) {
        partidoService.crear(partido);
    }

    @PUT
    public void actualizar(Partido partido) {
        partidoService.actualizar(partido);
    }

    @DELETE
    @Path("/{id}")
    public void eliminar(@PathParam("id") Integer id) {
        partidoService.eliminar(id);
    }
}
