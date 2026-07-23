package ec.utn.golmundial.estadisticas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resultado")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado")
    private Integer idResultado;

    @OneToOne
    @JoinColumn(name = "id_partido")
    private Partido partido;

    @Column(name = "goles_local", nullable = false)
    private int golesLocal;

    @Column(name = "goles_visitante", nullable = false)
    private int golesVisitante;

    // ===== GETTERS =====

    public Integer getIdResultado() {
        return idResultado;
    }

    public Partido getPartido() {
        return partido;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    // ===== SETTERS =====

    public void setIdResultado(Integer idResultado) {
        this.idResultado = idResultado;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }
}

