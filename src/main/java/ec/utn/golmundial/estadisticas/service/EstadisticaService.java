package ec.utn.golmundial.estadisticas.service;

import ec.utn.golmundial.estadisticas.entity.EstadisticaSeleccion;
import ec.utn.golmundial.estadisticas.entity.Resultado;
import ec.utn.golmundial.estadisticas.repository.EstadisticaSeleccionRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class EstadisticaService {

    @Inject
    private EstadisticaSeleccionRepository repo;

    public void actualizarEstadisticas(Resultado resultado) {

        var local = resultado.getPartido().getSeleccionLocal();
        var visitante = resultado.getPartido().getSeleccionVisitante();

        EstadisticaSeleccion estLocal = obtenerOcrear(local.getIdSeleccion());
        EstadisticaSeleccion estVisitante = obtenerOcrear(visitante.getIdSeleccion());

        // Partidos jugados
        estLocal.setPartidosJugados(estLocal.getPartidosJugados() + 1);
        estVisitante.setPartidosJugados(estVisitante.getPartidosJugados() + 1);

        // Goles
        estLocal.setGolesFavor(estLocal.getGolesFavor() + resultado.getGolesLocal());
        estLocal.setGolesContra(estLocal.getGolesContra() + resultado.getGolesVisitante());

        estVisitante.setGolesFavor(estVisitante.getGolesFavor() + resultado.getGolesVisitante());
        estVisitante.setGolesContra(estVisitante.getGolesContra() + resultado.getGolesLocal());

        // Resultado del partido
        if (resultado.getGolesLocal() > resultado.getGolesVisitante()) {
            estLocal.setGanados(estLocal.getGanados() + 1);
            estVisitante.setPerdidos(estVisitante.getPerdidos() + 1);
            estLocal.setPuntos(estLocal.getPuntos() + 3);
        } else if (resultado.getGolesLocal() < resultado.getGolesVisitante()) {
            estVisitante.setGanados(estVisitante.getGanados() + 1);
            estLocal.setPerdidos(estLocal.getPerdidos() + 1);
            estVisitante.setPuntos(estVisitante.getPuntos() + 3);
        } else {
            estLocal.setEmpatados(estLocal.getEmpatados() + 1);
            estVisitante.setEmpatados(estVisitante.getEmpatados() + 1);
            estLocal.setPuntos(estLocal.getPuntos() + 1);
            estVisitante.setPuntos(estVisitante.getPuntos() + 1);
        }

        repo.guardar(estLocal);
        repo.guardar(estVisitante);
    }

    private EstadisticaSeleccion obtenerOcrear(Integer idSeleccion) {
        EstadisticaSeleccion est = repo.buscarPorSeleccion(idSeleccion);
        if (est == null) {
            est = new EstadisticaSeleccion();
            est.setSeleccion(repo.obtenerReferenciaSeleccion(idSeleccion));
        }
        return est;
    }
}
