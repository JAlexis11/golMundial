package com.utn.golmundial.publicfrontend.model;

import java.io.Serializable;

public class RankingEntry implements Serializable {

    private Long usuarioId;
    private Double saldo;
    private Integer aciertos;
    private Integer totalPredicciones;

    public RankingEntry() {
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

    public Integer getAciertos() {
        return aciertos;
    }

    public void setAciertos(Integer aciertos) {
        this.aciertos = aciertos;
    }

    public Integer getTotalPredicciones() {
        return totalPredicciones;
    }

    public void setTotalPredicciones(Integer totalPredicciones) {
        this.totalPredicciones = totalPredicciones;
    }

}