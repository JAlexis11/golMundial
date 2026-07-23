package ec.utn.golmundial.estadisticas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAuditoria;

    @Column(name = "tabla_afectada", nullable = false)
    private String tablaAfectada;

    @Column(name = "id_registro", nullable = false)
    private Integer idRegistro;

    private String accion;
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "id_usuario_actor")
    private Usuario usuarioActor;

    // ===== GETTERS =====

    public Integer getIdAuditoria() {
        return idAuditoria;
    }

    public String getTablaAfectada() {
        return tablaAfectada;
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public String getAccion() {
        return accion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Usuario getUsuarioActor() {
        return usuarioActor;
    }

    // ===== SETTERS =====

    public void setIdAuditoria(Integer idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public void setTablaAfectada(String tablaAfectada) {
        this.tablaAfectada = tablaAfectada;
    }

    public void setIdRegistro(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setUsuarioActor(Usuario usuarioActor) {
        this.usuarioActor = usuarioActor;
    }
}

