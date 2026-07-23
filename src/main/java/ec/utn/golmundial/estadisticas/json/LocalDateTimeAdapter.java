package ec.utn.golmundial.estadisticas.json;

import jakarta.json.bind.adapter.JsonbAdapter;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

// Clientes como .NET serializan DateTime con offset y hasta 7 dígitos de fracción
// de segundo (ej. "2026-07-23T15:09:42.4335295-05:00"), que el parser default de
// Yasson para LocalDateTime no acepta (no maneja offset). Se intenta el parseo
// simple primero y, si falla, se interpreta como OffsetDateTime y se descarta la zona.
public class LocalDateTimeAdapter implements JsonbAdapter<LocalDateTime, String> {

    @Override
    public String adaptToJson(LocalDateTime obj) {
        return obj == null ? null : obj.toString();
    }

    @Override
    public LocalDateTime adaptFromJson(String obj) {
        if (obj == null || obj.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(obj);
        } catch (DateTimeParseException e) {
            return OffsetDateTime.parse(obj).toLocalDateTime();
        }
    }
}
