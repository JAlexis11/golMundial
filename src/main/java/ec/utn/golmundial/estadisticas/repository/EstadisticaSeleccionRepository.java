package ec.utn.golmundial.estadisticas.repository;

import ec.utn.golmundial.estadisticas.dto.EstadisticaDTO;
import ec.utn.golmundial.estadisticas.entity.EstadisticaSeleccion;
import ec.utn.golmundial.estadisticas.entity.Seleccion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class EstadisticaSeleccionRepository {

    @PersistenceContext(unitName = "estadisticasPU")
    private EntityManager em;

    public EstadisticaSeleccion buscarPorSeleccion(Integer idSeleccion) {
        return em.createQuery("SELECT e FROM EstadisticaSeleccion e WHERE e.seleccion.idSeleccion = :id",
                              EstadisticaSeleccion.class)
                 .setParameter("id", idSeleccion)
                 .getResultStream()
                 .findFirst()
                 .orElse(null);
    }

    // Referencia gestionada por JPA (sin SELECT) para asociar una FK sin cargar la entidad completa
    // ni arriesgar un TransientObjectException por pasar un objeto "a mano" con solo el id seteado.
    public Seleccion obtenerReferenciaSeleccion(Integer idSeleccion) {
        return em.getReference(Seleccion.class, idSeleccion);
    }

    public void guardar(EstadisticaSeleccion estadistica) {
        if (estadistica.getIdEstadistica() == null) {
            em.persist(estadistica);
        } else {
            em.merge(estadistica);
        }
    }

    public List<EstadisticaDTO> listarTodas() {
        return em.createQuery(
                "SELECT new ec.utn.golmundial.estadisticas.dto.EstadisticaDTO(" +
                "e.idEstadistica, s.idSeleccion, s.nombre, e.partidosJugados, e.ganados, " +
                "e.empatados, e.perdidos, e.golesFavor, e.golesContra, e.puntos) " +
                "FROM EstadisticaSeleccion e JOIN e.seleccion s " +
                "ORDER BY e.puntos DESC, (e.golesFavor - e.golesContra) DESC",
                EstadisticaDTO.class)
                .getResultList();
    }
}
