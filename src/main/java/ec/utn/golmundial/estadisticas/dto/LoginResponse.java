package ec.utn.golmundial.estadisticas.dto;

import java.time.LocalDateTime;

public class LoginResponse {
    private String token;
    private String rol;
    private LocalDateTime expiracion;
    private String email;
    private String username;
    private String fullName;

    public LoginResponse(String token, String rol, LocalDateTime expiracion,
                          String email, String username, String fullName) {
        this.token = token;
        this.rol = rol;
        this.expiracion = expiracion;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
    }

    public String getToken() { return token; }
    public String getRol() { return rol; }
    public LocalDateTime getExpiracion() { return expiracion; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
}
