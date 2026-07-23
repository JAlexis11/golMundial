package ec.utn.golmundial.estadisticas.repository;

import ec.utn.golmundial.estadisticas.entity.Resultado;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class ResultadoRepository {

    @PersistenceContext(unitName = "estadisticasPU")
    private EntityManager em;

    public void crear(Resultado resultado) {
        em.persist(resultado);
    }

    public Resultado buscarPorPartido(Integer idPartido) {
        return em.createQuery("SELECT r FROM Resultado r WHERE r.partido.idPartido = :id", Resultado.class)
                 .setParameter("id", idPartido)
                 .getResultStream()
                 .findFirst()
                 .orElse(null);
    }
}
