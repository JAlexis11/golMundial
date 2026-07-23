package ec.utn.golmundial.estadisticas.security;

import ec.utn.golmundial.estadisticas.entity.Usuario;
import jakarta.enterprise.context.RequestScoped;

/**
 * Bean de contexto de request (CDI @RequestScoped): AuthFilter resuelve el
 * Usuario admin del token Bearer y lo deja acá; los servicios que necesitan
 * saber "quién hizo la acción" (para Auditoria) lo inyectan y lo leen.
 */
@RequestScoped
public class UsuarioActual {

    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getIdUsuarioOrNull() {
        return usuario != null ? usuario.getIdUsuario() : null;
    }
}
