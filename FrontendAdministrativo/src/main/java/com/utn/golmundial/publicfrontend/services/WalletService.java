package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.Wallet;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class WalletService {

    // IP configurable en app.properties (backend.baseUrl)
    private static final String BASE_URL = AppConfig.getBackendBaseUrl();

    public Wallet obtenerBilletera(Long usuarioId) {

        Client client = ClientBuilder.newClient();

        try {

            Response response = client.target(BASE_URL)
                    .path("/api/Billeteras/" + usuarioId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                return response.readEntity(Wallet.class);
            }

            return null;

        } finally {
            client.close();
        }
    }

    public Wallet crearBilletera(Long usuarioId) {

        Client client = ClientBuilder.newClient();

        try {

            String body = "{\"usuarioId\":" + usuarioId + "}";

            Response response = client.target(BASE_URL)
                    .path("/api/Billeteras")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(body, MediaType.APPLICATION_JSON));

            if (response.getStatus() == 201) {
                return response.readEntity(Wallet.class);
            }

            return null;

        } finally {
            client.close();
        }
    }
}