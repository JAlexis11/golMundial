package ec.utn.golmundial.estadisticas.repository;

import ec.utn.golmundial.estadisticas.dto.SeleccionDTO;
import ec.utn.golmundial.estadisticas.dto.SeleccionResumenDTO;
import ec.utn.golmundial.estadisticas.entity.Seleccion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@Stateless
public class SeleccionRepository {

    @PersistenceContext(unitName = "estadisticasPU")
    private EntityManager em;

    public void crear(Seleccion seleccion) {
        em.persist(seleccion);
    }

    // Entidad gestionada: uso interno (actualizar/eliminar), nunca se serializa directo a un response.
    public Seleccion buscarPorId(Integer id) {
        return em.find(Seleccion.class, id);
    }

    public List<SeleccionDTO> listar() {
        return em.createQuery(
                "SELECT new ec.utn.golmundial.estadisticas.dto.SeleccionDTO(" +
                "s.idSeleccion, s.nombre, s.confederacion, g.nombre) " +
                "FROM Seleccion s JOIN s.grupo g ORDER BY s.nombre",
                SeleccionDTO.class)
                .getResultList();
    }

    public SeleccionDTO buscarDetallePorId(Integer id) {
        return em.createQuery(
                "SELECT new ec.utn.golmundial.estadisticas.dto.SeleccionDTO(" +
                "s.idSeleccion, s.nombre, s.confederacion, g.nombre) " +
                "FROM Seleccion s JOIN s.grupo g WHERE s.idSeleccion = :id",
                SeleccionDTO.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<SeleccionDTO> listarPorGrupo(String grupo) {
        return em.createQuery(
                "SELECT new ec.utn.golmundial.estadisticas.dto.SeleccionDTO(" +
                "s.idSeleccion, s.nombre, s.confederacion, g.nombre) " +
                "FROM Seleccion s JOIN s.grupo g WHERE g.nombre = :grupo ORDER BY s.nombre",
                SeleccionDTO.class)
                .setParameter("grupo", grupo)
                .getResultList();
    }

    public List<SeleccionResumenDTO> listarResumenPorGrupo(Integer idGrupo) {
        return em.createQuery(
                "SELECT new ec.utn.golmundial.estadisticas.dto.SeleccionResumenDTO(" +
                "s.idSeleccion, s.nombre, s.confederacion) " +
                "FROM Seleccion s WHERE s.grupo.idGrupo = :idGrupo ORDER BY s.nombre",
                SeleccionResumenDTO.class)
                .setParameter("idGrupo", idGrupo)
                .getResultList();
    }

    public void actualizar(Seleccion seleccion) {
        em.merge(seleccion);
    }

    public void eliminar(Integer id) {
        Seleccion s = buscarPorId(id);
        if (s == null) {
            throw new NotFoundException("La selección " + id + " no existe.");
        }
        em.remove(s);
    }
}

