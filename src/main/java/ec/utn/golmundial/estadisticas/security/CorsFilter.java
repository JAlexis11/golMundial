package ec.utn.golmundial.estadisticas.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

/**
 * Sin esto, cualquier frontend (Blazor de Fer, JSF de Dayana) corriendo en otro
 * origen/puerto queda bloqueado por el navegador, aunque el backend responda bien.
 * "*" es seguro acá porque la autenticación es por header Authorization (Bearer),
 * no por cookies — no hace falta Access-Control-Allow-Credentials.
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
    }
}
