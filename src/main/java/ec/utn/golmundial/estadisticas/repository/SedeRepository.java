package ec.utn.golmundial.estadisticas.repository;

import ec.utn.golmundial.estadisticas.entity.Sede;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class SedeRepository {

    @PersistenceContext(unitName = "estadisticasPU")
    private EntityManager em;

    public List<Sede> listar() {
        return em.createQuery("SELECT s FROM Sede s", Sede.class)
                 .getResultList();
    }
}
