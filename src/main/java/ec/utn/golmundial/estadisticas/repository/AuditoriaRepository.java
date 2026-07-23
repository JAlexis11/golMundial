package ec.utn.golmundial.estadisticas.repository;

import ec.utn.golmundial.estadisticas.entity.Auditoria;
import ec.utn.golmundial.estadisticas.entity.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class AuditoriaRepository {

    @PersistenceContext(unitName = "estadisticasPU")
    private EntityManager em;

    public void registrar(Auditoria auditoria) {
        em.persist(auditoria);
    }

    // Referencia gestionada (sin SELECT) para asociar el actor sin arriesgar
    // el mismo TransientObjectException evitado en EstadisticaSeleccionRepository.
    public Usuario obtenerReferenciaUsuario(Integer idUsuario) {
        return em.getReference(Usuario.class, idUsuario);
    }
}
