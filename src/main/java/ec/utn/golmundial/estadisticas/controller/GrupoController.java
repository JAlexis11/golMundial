package ec.utn.golmundial.estadisticas.controller;

import ec.utn.golmundial.estadisticas.dto.GrupoDTO;
import ec.utn.golmundial.estadisticas.service.GrupoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/grupos")
@Produces(MediaType.APPLICATION_JSON)
public class GrupoController {

    @Inject
    private GrupoService grupoService;

    @GET
    public List<GrupoDTO> listar() {
        return grupoService.listar();
    }
}
