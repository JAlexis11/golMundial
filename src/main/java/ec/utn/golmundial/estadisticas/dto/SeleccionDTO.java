package ec.utn.golmundial.estadisticas.dto;

public class SeleccionDTO {
    private Integer idSeleccion;
    private String nombre;
    private String confederacion;
    private String grupo;

    public SeleccionDTO(Integer idSeleccion, String nombre, String confederacion, String grupo) {
        this.idSeleccion = idSeleccion;
        this.nombre = nombre;
        this.confederacion = confederacion;
        this.grupo = grupo;
    }

    public Integer getIdSeleccion() { return idSeleccion; }
    public String getNombre() { return nombre; }
    public String getConfederacion() { return confederacion; }
    public String getGrupo() { return grupo; }
}
