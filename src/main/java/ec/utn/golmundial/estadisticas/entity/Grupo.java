package ec.utn.golmundial.estadisticas.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "grupo")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    private Integer idGrupo;

    @Column(nullable = false, length = 50)
    private String nombre; // "Grupo A", "Grupo B", ...

    @OneToMany(mappedBy = "grupo")
    private List<Seleccion> selecciones;

    // ===== GETTERS =====

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Seleccion> getSelecciones() {
        return selecciones;
    }

    // ===== SETTERS =====

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setSelecciones(List<Seleccion> selecciones) {
        this.selecciones = selecciones;
    }
}


