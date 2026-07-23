package ec.utn.golmundial.estadisticas.json;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JsonbConfigProvider implements ContextResolver<Jsonb> {

    private final Jsonb jsonb = JsonbBuilder.create(
            new JsonbConfig().withAdapters(new LocalDateTimeAdapter()));

    @Override
    public Jsonb getContext(Class<?> type) {
        return jsonb;
    }
}
