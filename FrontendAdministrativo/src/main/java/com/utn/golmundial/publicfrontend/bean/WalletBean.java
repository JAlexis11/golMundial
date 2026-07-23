package com.utn.golmundial.publicfrontend.bean;

import com.utn.golmundial.publicfrontend.model.Wallet;
import com.utn.golmundial.publicfrontend.services.AppConfig;
import com.utn.golmundial.publicfrontend.services.WalletService;
import com.utn.golmundial.publicfrontend.services.WalletServiceMock;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class WalletBean implements Serializable {

    private static final boolean usarMock = false;

    private final WalletService walletService = new WalletService();
    private final WalletServiceMock walletServiceMock = new WalletServiceMock();

    private Wallet wallet;

    // Usuario de prueba, configurable en app.properties (backend.usuarioId)
    private Long usuarioId = AppConfig.getUsuarioIdDemo();

    @PostConstruct
    public void init() {

        if (usarMock) {
            wallet = walletServiceMock.obtenerBilletera(usuarioId);
        } else {
            wallet = walletService.obtenerBilletera(usuarioId);
        }

    }

    public Wallet getWallet() {
        return wallet;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
}