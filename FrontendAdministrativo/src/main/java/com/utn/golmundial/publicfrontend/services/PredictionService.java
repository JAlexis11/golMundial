package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.Prediction;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

public class PredictionService {

    // IP configurable en app.properties (backend.baseUrl)
    private static final String BASE_URL = AppConfig.getBackendBaseUrl();

    public Prediction crearApuesta(Prediction datos) {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(BASE_URL)
                    .path("/api/Predicciones")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(datos, MediaType.APPLICATION_JSON));

            int status = response.getStatus();
            String respuesta = response.readEntity(String.class);

            System.out.println("==================================");
            System.out.println("Código HTTP: " + status);
            System.out.println("Respuesta:");
            System.out.println(respuesta);
            System.out.println("==================================");

            // La API respondió OK (200) o Creado (201) -> la predicción sí se guardó
            if (status == 200 || status == 201) {
                // Devolvemos los mismos datos que mandamos, ya que el status HTTP
                // confirma que la API la creó correctamente.
                return datos;
            }

            // Status de error (400, 409, 500, etc.) -> no se creó
            return null;

        } finally {
            client.close();
        }
    }

    public List<Prediction> obtenerApuestasUsuario(Long usuarioId) {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(BASE_URL)
                    .path("/api/Predicciones/usuario/" + usuarioId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                return response.readEntity(new GenericType<List<Prediction>>() {
                });
            }
            return List.of();
        } finally {
            client.close();
        }
    }
}