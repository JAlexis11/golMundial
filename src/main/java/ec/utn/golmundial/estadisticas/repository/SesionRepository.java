package ec.utn.golmundial.estadisticas.repository;

import ec.utn.golmundial.estadisticas.entity.Sesion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class SesionRepository {

    @PersistenceContext(unitName = "estadisticasPU")
    private EntityManager em;

    public void crear(Sesion sesion) {
        em.persist(sesion);
    }

    public Sesion buscarPorToken(String token) {
        return em.find(Sesion.class, token);
    }

    public void eliminar(String token) {
        Sesion sesion = buscarPorToken(token);
        if (sesion != null) {
            em.remove(sesion);
        }
    }
}
