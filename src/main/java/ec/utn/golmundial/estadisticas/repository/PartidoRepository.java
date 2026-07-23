package ec.utn.golmundial.estadisticas.repository;

import ec.utn.golmundial.estadisticas.dto.PartidoDTO;
import ec.utn.golmundial.estadisticas.entity.FaseEnum;
import ec.utn.golmundial.estadisticas.entity.Partido;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@Stateless
public class PartidoRepository {

    @PersistenceContext(unitName = "estadisticasPU")
    private EntityManager em;

    // Entidad gestionada: uso interno (ResultadoService), nunca se serializa directo a un response.
    public Partido buscarPorId(Integer id) {
        return em.find(Partido.class, id);
    }

    public List<PartidoDTO> listar() {
        return em.createQuery(
                "SELECT new ec.utn.golmundial.estadisticas.dto.PartidoDTO(" +
                "p.idPartido, sl.nombre, sv.nombre, se.ciudad, p.fecha, p.fase) " +
                "FROM Partido p " +
                "JOIN p.seleccionLocal sl " +
                "JOIN p.seleccionVisitante sv " +
                "JOIN p.sede se " +
                "ORDER BY p.fecha",
                PartidoDTO.class)
                .getResultList();
    }

    // ⭐ LISTAR POR FASE
    public List<PartidoDTO> listarPorFase(FaseEnum fase) {
        return em.createQuery(
                "SELECT new ec.utn.golmundial.estadisticas.dto.PartidoDTO(" +
                "p.idPartido, sl.nombre, sv.nombre, se.ciudad, p.fecha, p.fase) " +
                "FROM Partido p " +
                "JOIN p.seleccionLocal sl " +
                "JOIN p.seleccionVisitante sv " +
                "JOIN p.sede se " +
                "WHERE p.fase = :fase ORDER BY p.fecha",
                PartidoDTO.class)
                .setParameter("fase", fase)
                .getResultList();
    }

    public List<PartidoDTO> listarDetalladoPorSeleccion(Integer idSeleccion) {
        return em.createQuery(
                "SELECT new ec.utn.golmundial.estadisticas.dto.PartidoDTO(" +
                "p.idPartido, sl.nombre, sv.nombre, se.ciudad, p.fecha, p.fase) " +
                "FROM Partido p " +
                "JOIN p.seleccionLocal sl " +
                "JOIN p.seleccionVisitante sv " +
                "JOIN p.sede se " +
                "WHERE sl.idSeleccion = :id OR sv.idSeleccion = :id " +
                "ORDER BY p.fecha",
                PartidoDTO.class)
                .setParameter("id", idSeleccion)
                .getResultList();
    }

    public void crear(Partido partido) {
        em.persist(partido);
    }

    public void actualizar(Partido partido) {
        em.merge(partido);
    }

    public void eliminar(Integer id) {
        Partido p = buscarPorId(id);
        if (p == null) {
            throw new NotFoundException("El partido " + id + " no existe.");
        }
        em.remove(p);
    }
}

