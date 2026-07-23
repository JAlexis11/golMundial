package ec.utn.golmundial.estadisticas.entity;

import java.util.List;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;

@Entity
@Table(name = "sede")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSede;
    
    @OneToMany(mappedBy = "sede")
    @JsonbTransient
    private List<Partido> partidos;


    private String ciudad;
    private String estadio;

    // ===== GETTERS =====

    public Integer getIdSede() {
        return idSede;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getEstadio() {
        return estadio;
    }

    // ===== SETTERS =====

    public void setIdSede(Integer idSede) {
        this.idSede = idSede;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }
}
