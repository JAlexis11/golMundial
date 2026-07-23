package com.utn.golmundial.publicfrontend.model;

import java.io.Serializable;

public class Prediction implements Serializable {

    private Long id;
    private Long usuarioId;
    private Long partidoId;
    private String fechaInicioPartido;
    private String pronostico;
    private Double monto;
    private Double cuota;
    private String estado;
    private String fecha;

    public Prediction() {
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
    public Long getPartidoId() { 
        return partidoId; 
    }
    public void setPartidoId(Long partidoId) { 
        this.partidoId = partidoId; 
    }
    public String getFechaInicioPartido() { 
        return fechaInicioPartido; 
    }
    public void setFechaInicioPartido(String fechaInicioPartido) { 
        this.fechaInicioPartido = fechaInicioPartido; 
    }
    public String getPronostico() { 
        return pronostico; 
    }
    public void setPronostico(String pronostico) { 
        this.pronostico = pronostico; 
    }
    public Double getMonto() { 
        return monto; 
    }
    public void setMonto(Double monto) { 
        this.monto = monto; 
    }
    public Double getCuota() { 
        return cuota; 
    }
    public void setCuota(Double cuota) { 
        this.cuota = cuota; 
    }
    public String getEstado() { 
        return estado; 
    }
    public void setEstado(String estado) { 
        this.estado = estado; 
    }
    public String getFecha() { 
        return fecha; 
    }
    public void setFecha(String fecha) { 
        this.fecha = fecha; 
    }
}