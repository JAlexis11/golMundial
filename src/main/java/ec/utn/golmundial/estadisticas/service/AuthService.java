package ec.utn.golmundial.estadisticas.service;

import ec.utn.golmundial.estadisticas.dto.LoginResponse;
import ec.utn.golmundial.estadisticas.entity.RolEnum;
import ec.utn.golmundial.estadisticas.entity.Sesion;
import ec.utn.golmundial.estadisticas.entity.Usuario;
import ec.utn.golmundial.estadisticas.integration.UtnGolCoinClient;
import ec.utn.golmundial.estadisticas.repository.SesionRepository;
import ec.utn.golmundial.estadisticas.repository.UsuarioRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.UUID;

@Stateless
public class AuthService {

    private static final int HORAS_EXPIRACION_SESION = 2;

    @Inject
    private UsuarioRepository usuarioRepository;

    @Inject
    private SesionRepository sesionRepository;

    @Inject
    private UtnGolCoinClient utnGolCoinClient;

    @Inject
    private AuditoriaService auditoriaService;

    public Usuario registrar(String email, String username, String fullName, String password) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es requerido.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es requerido.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("El nombre completo es requerido.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        if (usuarioRepository.buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese email.");
        }
        if (usuarioRepository.buscarPorUsername(username) != null) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese nombre de usuario.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setUsername(username);
        usuario.setFullName(fullName);
        usuario.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        usuario.setRol(RolEnum.USUARIO_REGISTRADO);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuarioRepository.crear(usuario);

        auditoriaService.registrar("usuario", usuario.getIdUsuario(), "REGISTRO", usuario.getIdUsuario());

        // RF01: solo USUARIO_REGISTRADO tiene billetera — INVITADO no apuesta, no la necesita.
        // Degradación controlada (RNF05): si UTNGolCoin no responde, el registro local ya
        // quedó guardado y no se revierte por esto.
        if (usuario.getRol() == RolEnum.USUARIO_REGISTRADO) {
            utnGolCoinClient.crearBilletera(usuario.getIdUsuario());
        }

        return usuario;
    }

    public LoginResponse login(String email, String password) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email);
        if (usuario == null || !BCrypt.checkpw(password, usuario.getPasswordHash())) {
            throw new NotAuthorizedException("Email o contraseña inválidos.", "Bearer");
        }

        Sesion sesion = new Sesion();
        sesion.setToken(UUID.randomUUID().toString());
        sesion.setUsuario(usuario);
        sesion.setFechaCreacion(LocalDateTime.now());
        sesion.setFechaExpiracion(LocalDateTime.now().plusHours(HORAS_EXPIRACION_SESION));
        sesionRepository.crear(sesion);

        return new LoginResponse(sesion.getToken(), usuario.getRol().name(), sesion.getFechaExpiracion(),
                usuario.getEmail(), usuario.getUsername(), usuario.getFullName());
    }

    public void logout(String token) {
        sesionRepository.eliminar(token);
    }

    public Usuario validarToken(String token) {
        if (token == null) {
            return null;
        }
        Sesion sesion = sesionRepository.buscarPorToken(token);
        if (sesion == null || sesion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return null;
        }
        return sesion.getUsuario();
    }
}
