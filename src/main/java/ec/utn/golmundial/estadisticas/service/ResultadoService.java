package ec.utn.golmundial.estadisticas.service;

import ec.utn.golmundial.estadisticas.entity.Partido;
import ec.utn.golmundial.estadisticas.entity.Resultado;
import ec.utn.golmundial.estadisticas.integration.UtnGolCoinClient;
import ec.utn.golmundial.estadisticas.repository.ResultadoRepository;
import ec.utn.golmundial.estadisticas.security.UsuarioActual;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class ResultadoService {

    @Inject
    private ResultadoRepository repo;

    @Inject
    private PartidoService partidoService;

    @Inject
    private EstadisticaService estadisticaService;

    @Inject
    private AuditoriaService auditoriaService;

    @Inject
    private UtnGolCoinClient utnGolCoinClient;

    @Inject
    private UsuarioActual usuarioActual;

    public void registrarResultado(Resultado resultado) {

        if (resultado.getPartido() == null || resultado.getPartido().getIdPartido() == null) {
            throw new IllegalArgumentException("Debe indicar el partido del resultado.");
        }
        if (resultado.getGolesLocal() < 0 || resultado.getGolesVisitante() < 0) {
            throw new IllegalArgumentException("Los goles no pueden ser negativos.");
        }

        Partido partido = partidoService.buscarPorId(resultado.getPartido().getIdPartido());

        if (partido == null) {
            throw new IllegalArgumentException("El partido no existe");
        }

        Resultado existente = repo.buscarPorPartido(partido.getIdPartido());
        if (existente != null) {
            throw new IllegalArgumentException("Este partido ya tiene resultado registrado");
        }

        // Reasignar la entidad gestionada: resultado.getPartido() viene del JSON del request
        // (deserializado, no gestionado por JPA) — persistir sin este reemplazo dispara
        // TransientObjectException al hacer flush del @OneToOne hacia un Partido "suelto".
        resultado.setPartido(partido);

        repo.crear(resultado);

        estadisticaService.actualizarEstadisticas(resultado);

        auditoriaService.registrar("resultado", resultado.getIdResultado(), "CREAR", usuarioActual.getIdUsuarioOrNull());

        utnGolCoinClient.notificarLiquidacion(partido.getIdPartido(), determinarResultado(resultado));
    }

    private String determinarResultado(Resultado resultado) {
        if (resultado.getGolesLocal() > resultado.getGolesVisitante()) {
            return "LOCAL";
        }
        if (resultado.getGolesLocal() < resultado.getGolesVisitante()) {
            return "VISITANTE";
        }
        return "EMPATE";
    }
}
