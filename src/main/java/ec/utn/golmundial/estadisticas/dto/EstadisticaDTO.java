package ec.utn.golmundial.estadisticas.dto;

public class EstadisticaDTO {
    private Integer idEstadistica;
    private Integer idSeleccion;
    private String nombreSeleccion;
    private int partidosJugados;
    private int ganados;
    private int empatados;
    private int perdidos;
    private int golesFavor;
    private int golesContra;
    private int puntos;

    public EstadisticaDTO(Integer idEstadistica, Integer idSeleccion, String nombreSeleccion,
                           int partidosJugados, int ganados, int empatados, int perdidos,
                           int golesFavor, int golesContra, int puntos) {
        this.idEstadistica = idEstadistica;
        this.idSeleccion = idSeleccion;
        this.nombreSeleccion = nombreSeleccion;
        this.partidosJugados = partidosJugados;
        this.ganados = ganados;
        this.empatados = empatados;
        this.perdidos = perdidos;
        this.golesFavor = golesFavor;
        this.golesContra = golesContra;
        this.puntos = puntos;
    }

    public Integer getIdEstadistica() { return idEstadistica; }
    public Integer getIdSeleccion() { return idSeleccion; }
    public String getNombreSeleccion() { return nombreSeleccion; }
    public int getPartidosJugados() { return partidosJugados; }
    public int getGanados() { return ganados; }
    public int getEmpatados() { return empatados; }
    public int getPerdidos() { return perdidos; }
    public int getGolesFavor() { return golesFavor; }
    public int getGolesContra() { return golesContra; }
    public int getPuntos() { return puntos; }
}
