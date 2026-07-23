package com.utn.golmundial.publicfrontend.model;

import jakarta.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class Venue implements Serializable {

    private Integer id;

    @JsonbProperty("nombre")
    private String name;

    @JsonbProperty("ciudad")
    private String city;

    @JsonbProperty("pais")
    private String country;

    public Integer getId() { 
        return id; 
    }
    public void setId(Integer id) { 
        this.id = id; 
    }
    public String getName() { 
        return name; 
    }
    public void setName(String name) { 
        this.name = name; 
    }
    public String getCity() { 
        return city; 
    }
    public void setCity(String city) { 
        this.city = city;
    }

    public String getCountry() { 
        return country; 
    }
    public void setCountry(String country) {
        this.country = country; 
    }
}