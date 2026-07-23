package com.utn.golmundial.publicfrontend.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Lee app.properties una sola vez. Ahi cada quien configura la IP de SU
// backend y el usuario de prueba, sin tener que tocar el codigo Java.
public final class AppConfig {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = AppConfig.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer app.properties", e);
        }
    }

    private AppConfig() {
    }

    public static String getBackendBaseUrl() {
        return PROPS.getProperty("backend.baseUrl", "http://localhost:5253");
    }

    public static Long getUsuarioIdDemo() {
        return Long.parseLong(PROPS.getProperty("backend.usuarioId", "100"));
    }
}
