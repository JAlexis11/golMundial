package ec.utn.golmundial.estadisticas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partido")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partido")
    private Integer idPartido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seleccion_local")
    private Seleccion seleccionLocal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seleccion_visitante")
    private Seleccion seleccionVisitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede")
    private Sede sede;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "fase", length = 255)
    private FaseEnum fase;

    // ===== GETTERS =====

    public Integer getIdPartido() {
        return idPartido;
    }

    public Seleccion getSeleccionLocal() {
        return seleccionLocal;
    }

    public Seleccion getSeleccionVisitante() {
        return seleccionVisitante;
    }

    public Sede getSede() {
        return sede;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public FaseEnum getFase() {
        return fase;
    }

    // ===== SETTERS =====

    public void setIdPartido(Integer idPartido) {
        this.idPartido = idPartido;
    }

    public void setSeleccionLocal(Seleccion seleccionLocal) {
        this.seleccionLocal = seleccionLocal;
    }

    public void setSeleccionVisitante(Seleccion seleccionVisitante) {
        this.seleccionVisitante = seleccionVisitante;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setFase(FaseEnum fase) {
        this.fase = fase;
    }
}