package ec.utn.golmundial.estadisticas.controller;

import ec.utn.golmundial.estadisticas.entity.Resultado;
import ec.utn.golmundial.estadisticas.service.ResultadoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/resultados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResultadoController {

    @Inject
    private ResultadoService service;

    @POST
    public void registrar(Resultado resultado) {
        service.registrarResultado(resultado);
    }
}
