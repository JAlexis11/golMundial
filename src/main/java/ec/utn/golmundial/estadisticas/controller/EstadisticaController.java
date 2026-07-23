package ec.utn.golmundial.estadisticas.controller;

import ec.utn.golmundial.estadisticas.dto.EstadisticaDTO;
import ec.utn.golmundial.estadisticas.repository.EstadisticaSeleccionRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/estadisticas")
@Produces(MediaType.APPLICATION_JSON)
public class EstadisticaController {

    @Inject
    private EstadisticaSeleccionRepository repo;

    @GET
    public List<EstadisticaDTO> listar() {
        return repo.listarTodas();
    }
}

