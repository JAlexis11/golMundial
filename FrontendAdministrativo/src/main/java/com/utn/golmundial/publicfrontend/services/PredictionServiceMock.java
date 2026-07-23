package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.Prediction;
import java.util.ArrayList;
import java.util.List;

public class PredictionServiceMock {

    public Prediction crearApuesta(Prediction datos) {
        datos.setId(1L);
        datos.setCuota(2.0);
        datos.setEstado("PENDIENTE");
        datos.setFecha("2026-07-22T00:00:00Z");
        return datos;
    }

    public List<Prediction> obtenerApuestasUsuario(Long usuarioId) {
        Prediction p = new Prediction();
        p.setId(1L);
        p.setUsuarioId(usuarioId);
        p.setPartidoId(9951L);
        p.setPronostico("LOCAL");
        p.setMonto(4.0);
        p.setCuota(2.0);
        p.setEstado("PENDIENTE");
        p.setFecha("2026-07-22T00:00:00Z");

        List<Prediction> lista = new ArrayList<>();
        lista.add(p);
        return lista;
    }
}