package ec.utn.golmundial.estadisticas.dto;

import ec.utn.golmundial.estadisticas.entity.FaseEnum;
import java.time.LocalDateTime;

public class PartidoDTO {
    private Integer idPartido;
    private String seleccionLocal;
    private String seleccionVisitante;
    private String sede;
    private LocalDateTime fecha;
    private FaseEnum fase;

    public PartidoDTO(Integer idPartido, String seleccionLocal, String seleccionVisitante,
                      String sede, LocalDateTime fecha, FaseEnum fase) {
        this.idPartido = idPartido;
        this.seleccionLocal = seleccionLocal;
        this.seleccionVisitante = seleccionVisitante;
        this.sede = sede;
        this.fecha = fecha;
        this.fase = fase;
    }

    // Getters
    public Integer getIdPartido() { return idPartido; }
    public String getSeleccionLocal() { return seleccionLocal; }
    public String getSeleccionVisitante() { return seleccionVisitante; }
    public String getSede() { return sede; }
    public LocalDateTime getFecha() { return fecha; }
    public FaseEnum getFase() { return fase; }
}
