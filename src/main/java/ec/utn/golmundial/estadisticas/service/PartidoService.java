package ec.utn.golmundial.estadisticas.service;

import ec.utn.golmundial.estadisticas.dto.PartidoDTO;
import ec.utn.golmundial.estadisticas.entity.FaseEnum;
import ec.utn.golmundial.estadisticas.entity.Partido;
import ec.utn.golmundial.estadisticas.repository.PartidoRepository;
import ec.utn.golmundial.estadisticas.repository.ResultadoRepository;
import ec.utn.golmundial.estadisticas.security.UsuarioActual;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@Stateless
public class PartidoService {

    @Inject
    private PartidoRepository partidoRepository;

    @Inject
    private ResultadoRepository resultadoRepository;

    @Inject
    private AuditoriaService auditoriaService;

    @Inject
    private UsuarioActual usuarioActual;

    // Listar todos
    public List<PartidoDTO> listar() {
        return partidoRepository.listar();
    }

    // Buscar por ID
    public Partido buscarPorId(Integer id) {
        return partidoRepository.buscarPorId(id);
    }

    // Listar por fase
    public List<PartidoDTO> listarPorFase(FaseEnum fase) {
        return partidoRepository.listarPorFase(fase);
    }

    // Listar por selección con DTO (detallado)
    public List<PartidoDTO> listarDetalladoPorSeleccion(Integer idSeleccion) {
        return partidoRepository.listarDetalladoPorSeleccion(idSeleccion);
    }

    public void crear(Partido partido) {
        // El front puede mandar idPartido (ej. 0, o un id viejo de un form reciclado):
        // con GenerationType.IDENTITY, persist() exige id nulo o Hibernate lo trata
        // como entidad "detached" y tira EJBTransactionRolledbackException.
        partido.setIdPartido(null);
        validar(partido);
        partidoRepository.crear(partido);
        auditoriaService.registrar("partido", partido.getIdPartido(), "CREAR", usuarioActual.getIdUsuarioOrNull());
    }

    public void actualizar(Partido partido) {
        if (partido.getIdPartido() == null || partidoRepository.buscarPorId(partido.getIdPartido()) == null) {
            throw new NotFoundException("El partido a actualizar no existe.");
        }
        validar(partido);
        partidoRepository.actualizar(partido);
        auditoriaService.registrar("partido", partido.getIdPartido(), "ACTUALIZAR", usuarioActual.getIdUsuarioOrNull());
    }

    public void eliminar(Integer id) {
        if (resultadoRepository.buscarPorPartido(id) != null) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el partido " + id + ": ya tiene un resultado registrado.");
        }
        partidoRepository.eliminar(id);
        auditoriaService.registrar("partido", id, "ELIMINAR", usuarioActual.getIdUsuarioOrNull());
    }

    private void validar(Partido partido) {
        if (partido.getSeleccionLocal() == null || partido.getSeleccionLocal().getIdSeleccion() == null) {
            throw new IllegalArgumentException("Debe indicar la selección local.");
        }
        if (partido.getSeleccionVisitante() == null || partido.getSeleccionVisitante().getIdSeleccion() == null) {
            throw new IllegalArgumentException("Debe indicar la selección visitante.");
        }
        if (partido.getSeleccionLocal().getIdSeleccion().equals(partido.getSeleccionVisitante().getIdSeleccion())) {
            throw new IllegalArgumentException("La selección local y la visitante no pueden ser la misma.");
        }
        if (partido.getSede() == null || partido.getSede().getIdSede() == null) {
            throw new IllegalArgumentException("Debe indicar la sede.");
        }
        if (partido.getFecha() == null) {
            throw new IllegalArgumentException("Debe indicar la fecha del partido.");
        }
    }
}

