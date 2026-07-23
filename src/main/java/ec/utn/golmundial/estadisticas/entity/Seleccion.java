package ec.utn.golmundial.estadisticas.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "seleccion")
public class Seleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seleccion")
    private Integer idSeleccion;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String confederacion;

    // Relación con Grupo
    @ManyToOne
    @JoinColumn(name = "id_grupo")
    private Grupo grupo;

    // Relación con Partido (local)
    @OneToMany(mappedBy = "seleccionLocal")
    private List<Partido> partidosLocal;

    // Relación con Partido (visitante)
    @OneToMany(mappedBy = "seleccionVisitante")
    private List<Partido> partidosVisitante;

    // ===== GETTERS =====

    public Integer getIdSeleccion() {
        return idSeleccion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getConfederacion() {
        return confederacion;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public List<Partido> getPartidosLocal() {
        return partidosLocal;
    }

    public List<Partido> getPartidosVisitante() {
        return partidosVisitante;
    }

    // ===== SETTERS =====

    public void setIdSeleccion(Integer idSeleccion) {
        this.idSeleccion = idSeleccion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setConfederacion(String confederacion) {
        this.confederacion = confederacion;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public void setPartidosLocal(List<Partido> partidosLocal) {
        this.partidosLocal = partidosLocal;
    }

    public void setPartidosVisitante(List<Partido> partidosVisitante) {
        this.partidosVisitante = partidosVisitante;
    }
}
