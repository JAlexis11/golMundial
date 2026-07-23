package ec.utn.golmundial.estadisticas.dto;

public class SeleccionResumenDTO {
    private Integer idSeleccion;
    private String nombre;
    private String confederacion;

    public SeleccionResumenDTO(Integer idSeleccion, String nombre, String confederacion) {
        this.idSeleccion = idSeleccion;
        this.nombre = nombre;
        this.confederacion = confederacion;
    }

    public Integer getIdSeleccion() { return idSeleccion; }
    public String getNombre() { return nombre; }
    public String getConfederacion() { return confederacion; }
}
