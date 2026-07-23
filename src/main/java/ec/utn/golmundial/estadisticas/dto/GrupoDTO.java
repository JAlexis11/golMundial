package ec.utn.golmundial.estadisticas.dto;

import java.util.List;

public class GrupoDTO {
    private Integer idGrupo;
    private String nombre;
    private List<SeleccionResumenDTO> selecciones;

    public GrupoDTO(Integer idGrupo, String nombre) {
        this.idGrupo = idGrupo;
        this.nombre = nombre;
    }

    public Integer getIdGrupo() { return idGrupo; }
    public String getNombre() { return nombre; }
    public List<SeleccionResumenDTO> getSelecciones() { return selecciones; }
    public void setSelecciones(List<SeleccionResumenDTO> selecciones) { this.selecciones = selecciones; }
}
