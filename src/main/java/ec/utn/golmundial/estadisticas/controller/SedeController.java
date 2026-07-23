package ec.utn.golmundial.estadisticas.controller;

import ec.utn.golmundial.estadisticas.entity.Sede;
import ec.utn.golmundial.estadisticas.repository.SedeRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/sedes")
@Produces(MediaType.APPLICATION_JSON)
public class SedeController {

    @Inject
    private SedeRepository repo;

    @GET
    public List<Sede> listar() {
        return repo.listar();
    }
}
