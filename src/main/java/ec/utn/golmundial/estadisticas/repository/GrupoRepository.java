package ec.utn.golmundial.estadisticas.repository;

import ec.utn.golmundial.estadisticas.dto.GrupoDTO;
import ec.utn.golmundial.estadisticas.entity.Grupo;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class GrupoRepository {

    @PersistenceContext(unitName = "estadisticasPU")
    private EntityManager em;

    // Entidad gestionada: uso interno (SeleccionService), nunca se serializa directo a un response.
    public Grupo buscarPorId(Integer id) {
        return em.find(Grupo.class, id);
    }

    public List<GrupoDTO> listar() {
        return em.createQuery(
                "SELECT new ec.utn.golmundial.estadisticas.dto.GrupoDTO(g.idGrupo, g.nombre) " +
                "FROM Grupo g ORDER BY g.nombre",
                GrupoDTO.class)
                .getResultList();
    }
}
