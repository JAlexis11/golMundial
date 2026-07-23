package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.Wallet;

public class WalletServiceMock {

    public Wallet obtenerBilletera(Long usuarioId) {
        Wallet w = new Wallet();
        w.setId(1L);
        w.setUsuarioId(usuarioId);
        w.setSaldo(10.0);
        w.setFechaCreacion("2026-07-22T00:00:00Z");
        return w;
    }

    public Wallet crearBilletera(Long usuarioId) {
        return obtenerBilletera(usuarioId);
    }
}