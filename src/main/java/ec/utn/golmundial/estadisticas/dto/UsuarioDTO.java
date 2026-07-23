package ec.utn.golmundial.estadisticas.dto;

import java.time.LocalDateTime;

public class UsuarioDTO {
    private Integer idUsuario;
    private String email;
    private String username;
    private String fullName;
    private String rol;
    private LocalDateTime fechaRegistro;

    public UsuarioDTO(Integer idUsuario, String email, String username, String fullName,
                       String rol, LocalDateTime fechaRegistro) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRol() { return rol; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
