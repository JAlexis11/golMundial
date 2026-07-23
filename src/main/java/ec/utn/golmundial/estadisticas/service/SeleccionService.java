package ec.utn.golmundial.estadisticas.service;

import ec.utn.golmundial.estadisticas.dto.SeleccionDTO;
import ec.utn.golmundial.estadisticas.entity.Grupo;
import ec.utn.golmundial.estadisticas.entity.Seleccion;
import ec.utn.golmundial.estadisticas.repository.GrupoRepository;
import ec.utn.golmundial.estadisticas.repository.SeleccionRepository;
import ec.utn.golmundial.estadisticas.security.UsuarioActual;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@Stateless
public class SeleccionService {

    @Inject
    private SeleccionRepository seleccionRepository;

    @Inject
    private GrupoRepository grupoRepository;

    @Inject
    private AuditoriaService auditoriaService;

    @Inject
    private UsuarioActual usuarioActual;

    public SeleccionDTO buscarDetallePorId(Integer id) {
        return seleccionRepository.buscarDetallePorId(id);
    }

    public List<SeleccionDTO> listar() {
        return seleccionRepository.listar();
    }

    public List<SeleccionDTO> listarPorGrupo(String grupo) {
        return seleccionRepository.listarPorGrupo(grupo);
    }

    public void crear(Seleccion seleccion) {
        seleccion.setGrupo(resolverGrupo(seleccion.getGrupo()));
        seleccionRepository.crear(seleccion);
        auditoriaService.registrar("seleccion", seleccion.getIdSeleccion(), "CREAR", usuarioActual.getIdUsuarioOrNull());
    }

    public void actualizar(Seleccion seleccion) {
        if (seleccion.getIdSeleccion() == null
                || seleccionRepository.buscarPorId(seleccion.getIdSeleccion()) == null) {
            throw new NotFoundException("La selección a actualizar no existe.");
        }
        seleccion.setGrupo(resolverGrupo(seleccion.getGrupo()));
        seleccionRepository.actualizar(seleccion);
        auditoriaService.registrar("seleccion", seleccion.getIdSeleccion(), "ACTUALIZAR", usuarioActual.getIdUsuarioOrNull());
    }

    // El Grupo del JSON viene deserializado (no gestionado por JPA) — reasignar la entidad
    // gestionada evita un EntityNotFoundException opaco en flush y permite devolver
    // un 400 legible cuando el idGrupo enviado no existe.
    private Grupo resolverGrupo(Grupo grupoRecibido) {
        if (grupoRecibido == null || grupoRecibido.getIdGrupo() == null) {
            throw new IllegalArgumentException("Debe indicar el grupo de la selección.");
        }
        Grupo grupo = grupoRepository.buscarPorId(grupoRecibido.getIdGrupo());
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo " + grupoRecibido.getIdGrupo() + " no existe.");
        }
        return grupo;
    }

    public void eliminar(Integer id) {
        seleccionRepository.eliminar(id);
        auditoriaService.registrar("seleccion", id, "ELIMINAR", usuarioActual.getIdUsuarioOrNull());
    }
}
