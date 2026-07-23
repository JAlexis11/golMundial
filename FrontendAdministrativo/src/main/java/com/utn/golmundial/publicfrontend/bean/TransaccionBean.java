package com.utn.golmundial.publicfrontend.bean;

import com.utn.golmundial.publicfrontend.model.Transaccion;
import com.utn.golmundial.publicfrontend.services.AppConfig;
import com.utn.golmundial.publicfrontend.services.TransaccionService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class TransaccionBean implements Serializable {

    private final TransaccionService transaccionService = new TransaccionService();

    private List<Transaccion> transacciones = new ArrayList<>();

    // Usuario de prueba, configurable en app.properties (backend.usuarioId)
    private Long usuarioId = AppConfig.getUsuarioIdDemo();

   @PostConstruct
public void init() {

    transacciones = transaccionService.obtenerTransacciones(usuarioId);

    if (transacciones == null) {
        transacciones = new ArrayList<>();
    }

}

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

}