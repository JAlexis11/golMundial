package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.RankingEntry;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

public class RankingService {

    // IP configurable en app.properties (backend.baseUrl)
    private static final String BASE_URL = AppConfig.getBackendBaseUrl();

    public List<RankingEntry> obtenerRanking() {

        Client client = ClientBuilder.newClient();

        try {

            Response response = client.target(BASE_URL)
                    .path("/api/Ranking")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {

                return response.readEntity(new GenericType<List<RankingEntry>>() {
                });

            }

            return List.of();

        } finally {

            client.close();

        }

    }

}