package ec.utn.golmundial.estadisticas.controller;

import ec.utn.golmundial.estadisticas.dto.SeleccionDTO;
import ec.utn.golmundial.estadisticas.entity.Seleccion;
import ec.utn.golmundial.estadisticas.service.SeleccionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/selecciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeleccionController {

    @Inject
    private SeleccionService seleccionService;

    @GET
    public List<SeleccionDTO> listar() {
        return seleccionService.listar();
    }

    @GET
    @Path("/{id}")
    public SeleccionDTO buscarPorId(@PathParam("id") Integer id) {
        SeleccionDTO seleccion = seleccionService.buscarDetallePorId(id);
        if (seleccion == null) {
            throw new NotFoundException("La selección " + id + " no existe.");
        }
        return seleccion;
    }

    @GET
    @Path("/grupo/{grupo}")
    public List<SeleccionDTO> listarPorGrupo(@PathParam("grupo") String grupo) {
        return seleccionService.listarPorGrupo(grupo);
    }

    @POST
    public void crear(Seleccion seleccion) {
        seleccionService.crear(seleccion);
    }

    @PUT
    public void actualizar(Seleccion seleccion) {
        seleccionService.actualizar(seleccion);
    }

    @DELETE
    @Path("/{id}")
    public void eliminar(@PathParam("id") Integer id) {
        seleccionService.eliminar(id);
    }
}
