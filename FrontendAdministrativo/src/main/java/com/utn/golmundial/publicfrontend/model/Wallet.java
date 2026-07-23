package com.utn.golmundial.publicfrontend.model;

import java.io.Serializable;

public class Wallet implements Serializable {

    private Long id;
    private Long usuarioId;
    private Double saldo;
    private String fechaCreacion;

    public Wallet() {
    }

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public Long getUsuarioId() { 
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) { 
        this.usuarioId = usuarioId; 
    }
    public Double getSaldo() { 
        return saldo;
    }
    public void setSaldo(Double saldo) { 
        this.saldo = saldo; 
    }
    public String getFechaCreacion() { 
        return fechaCreacion; 
    }
    public void setFechaCreacion(String fechaCreacion) { 
        this.fechaCreacion = fechaCreacion; 
    }
}