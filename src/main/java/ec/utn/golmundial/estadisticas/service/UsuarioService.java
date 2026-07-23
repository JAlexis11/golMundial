package ec.utn.golmundial.estadisticas.service;

import ec.utn.golmundial.estadisticas.dto.UsuarioDTO;
import ec.utn.golmundial.estadisticas.entity.RolEnum;
import ec.utn.golmundial.estadisticas.repository.UsuarioRepository;
import ec.utn.golmundial.estadisticas.security.UsuarioActual;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class UsuarioService {

    @Inject
    private UsuarioRepository usuarioRepository;

    @Inject
    private AuditoriaService auditoriaService;

    @Inject
    private UsuarioActual usuarioActual;

    public List<UsuarioDTO> listar() {
        return usuarioRepository.listar().stream()
                .map(u -> new UsuarioDTO(u.getIdUsuario(), u.getEmail(), u.getUsername(), u.getFullName(),
                        u.getRol().name(), u.getFechaRegistro()))
                .toList();
    }

    public void cambiarRol(Integer id, String rolTexto) {
        RolEnum rol;
        try {
            rol = RolEnum.valueOf(rolTexto == null ? "" : rolTexto.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El rol debe ser ADMINISTRADOR, USUARIO_REGISTRADO o INVITADO.");
        }
        usuarioRepository.actualizarRol(id, rol);
        auditoriaService.registrar("usuario", id, "CAMBIAR_ROL:" + rol, usuarioActual.getIdUsuarioOrNull());
    }
}
