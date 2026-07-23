package ec.utn.golmundial.estadisticas.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception exception) {
        // Las excepciones que se tiran DENTRO de un @Stateless (service o repository)
        // llegan acá envueltas en jakarta.ejb.EJBException/EJBTransactionRolledbackException
        // — es el interceptor de transacciones del contenedor EJB, que envuelve
        // cualquier RuntimeException antes de propagarla. Sin este unwrap, un
        // NotFoundException o un IllegalArgumentException tirado desde un service
        // nunca se ve como tal acá — siempre cae al 500 genérico, aunque haya sido
        // una validación de negocio perfectamente normal (400/401/403/404).
        Throwable real = unwrap(exception);
        int status = resolveStatus(real);

        // Para 4xx el mensaje de la excepción es una validación de negocio pensada
        // para mostrarse (RNF10). Para 5xx (no controlado: bug, FK violada, timeout
        // de DB, etc.) NO se expone exception.getMessage() al cliente — puede traer
        // detalle interno (SQL, nombre de constraint, stacktrace) y la mayoría de
        // los endpoints son públicos (GET sin auth). El detalle real queda logueado
        // acá para poder debuggear del lado del servidor.
        String mensaje;
        if (status >= 500) {
            LOG.log(Level.SEVERE, "Error no controlado", exception);
            mensaje = "Ocurrió un error inesperado en el servidor.";
        } else {
            mensaje = real.getMessage() != null
                    ? real.getMessage()
                    : "Ocurrió un error inesperado en el servidor.";
        }

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("mensaje", mensaje))
                .build();
    }

    private Throwable unwrap(Throwable exception) {
        Throwable current = exception;
        int depth = 0;
        while (!(current instanceof WebApplicationException) && !(current instanceof IllegalArgumentException)
                && current.getCause() != null && current.getCause() != current && depth < 10) {
            current = current.getCause();
            depth++;
        }
        return current;
    }

    private int resolveStatus(Throwable exception) {
        if (exception instanceof WebApplicationException wae) {
            return wae.getResponse().getStatus();
        }
        if (exception instanceof IllegalArgumentException) {
            return 400;
        }
        return 500;
    }
}
