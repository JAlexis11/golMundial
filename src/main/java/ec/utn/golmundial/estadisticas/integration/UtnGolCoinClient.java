package ec.utn.golmundial.estadisticas.integration;

import jakarta.ejb.Stateless;
import jakarta.json.Json;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class UtnGolCoinClient {

    private static final Logger LOG = Logger.getLogger(UtnGolCoinClient.class.getName());

    private static final String BASE_URL = System.getenv()
            .getOrDefault("UTNGOLCOIN_BASE_URL", "http://localhost:5253");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    /**
     * RF11/RF12: notifica el resultado oficial de un partido para que UTNGolCoin
     * liquide las predicciones pendientes. Degradación controlada (RNF05): un
     * fallo de red o timeout se loguea y se ignora, nunca se propaga — no debe
     * revertir el resultado ni las estadísticas ya confirmadas de este lado.
     */
    public void notificarLiquidacion(Integer partidoId, String resultado) {
        String body = Json.createObjectBuilder()
                .add("partidoId", partidoId)
                .add("resultado", resultado)
                .build()
                .toString();
        enviar("/api/utngolcoin/liquidacion", body, partidoId);
    }

    /**
     * RF01: dispara la creación de la billetera del usuario recién registrado.
     * Mismo criterio de degradación controlada: el registro local ya se guardó
     * y no se revierte si UTNGolCoin no responde.
     */
    public void crearBilletera(Integer usuarioId) {
        String body = Json.createObjectBuilder()
                .add("usuarioId", usuarioId)
                .build()
                .toString();
        enviar("/api/billeteras", body, usuarioId);
    }

    private void enviar(String path, String body, Integer idReferencia) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(3))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                LOG.warning("UTNGolCoin respondió " + response.statusCode() + " en " + path
                        + " para id " + idReferencia + ": " + response.body());
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo contactar a UTNGolCoin en " + path
                    + " para id " + idReferencia + " (RNF05: se ignora, no afecta la operación local).", e);
        }
    }
}
