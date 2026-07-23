package ec.utn.golmundial.estadisticas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "estadistica_seleccion")
public class EstadisticaSeleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadistica")
    private Integer idEstadistica;

    @ManyToOne
    @JoinColumn(name = "id_seleccion", nullable = false)
    private Seleccion seleccion;

    @Column(name = "partidos_jugados")
    private int partidosJugados;
    private int ganados;
    private int empatados;
    private int perdidos;
    @Column(name = "goles_favor")
    private int golesFavor;
    @Column(name = "goles_contra")
    private int golesContra;
    private int puntos;

    // ===== GETTERS =====

    public Integer getIdEstadistica() {
        return idEstadistica;
    }

    public Seleccion getSeleccion() {
        return seleccion;
    }

    public int getPartidosJugados() {
        return partidosJugados;
    }

    public int getGanados() {
        return ganados;
    }

    public int getEmpatados() {
        return empatados;
    }

    public int getPerdidos() {
        return perdidos;
    }

    public int getGolesFavor() {
        return golesFavor;
    }

    public int getGolesContra() {
        return golesContra;
    }

    public int getPuntos() {
        return puntos;
    }

    // ===== SETTERS =====

    public void setIdEstadistica(Integer idEstadistica) {
        this.idEstadistica = idEstadistica;
    }

    public void setSeleccion(Seleccion seleccion) {
        this.seleccion = seleccion;
    }

    public void setPartidosJugados(int partidosJugados) {
        this.partidosJugados = partidosJugados;
    }

    public void setGanados(int ganados) {
        this.ganados = ganados;
    }

    public void setEmpatados(int empatados) {
        this.empatados = empatados;
    }

    public void setPerdidos(int perdidos) {
        this.perdidos = perdidos;
    }

    public void setGolesFavor(int golesFavor) {
        this.golesFavor = golesFavor;
    }

    public void setGolesContra(int golesContra) {
        this.golesContra = golesContra;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
}
