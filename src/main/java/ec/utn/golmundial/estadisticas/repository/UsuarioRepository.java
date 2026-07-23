package ec.utn.golmundial.estadisticas.repository;

import ec.utn.golmundial.estadisticas.entity.RolEnum;
import ec.utn.golmundial.estadisticas.entity.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@Stateless
public class UsuarioRepository {

    @PersistenceContext(unitName = "estadisticasPU")
    private EntityManager em;

    public void crear(Usuario usuario) {
        em.persist(usuario);
    }

    public Usuario buscarPorId(Integer id) {
        return em.find(Usuario.class, id);
    }

    public Usuario buscarPorEmail(String email) {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public Usuario buscarPorUsername(String username) {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<Usuario> listar() {
        return em.createQuery("SELECT u FROM Usuario u ORDER BY u.email", Usuario.class)
                .getResultList();
    }

    public void actualizarRol(Integer id, RolEnum rol) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) {
            throw new NotFoundException("El usuario " + id + " no existe.");
        }
        usuario.setRol(rol);
        em.merge(usuario);
    }
}
