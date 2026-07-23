package com.utn.golmundial.publicfrontend.bean;

import com.utn.golmundial.publicfrontend.model.Match;
import com.utn.golmundial.publicfrontend.model.Prediction;
import com.utn.golmundial.publicfrontend.services.AppConfig;
import com.utn.golmundial.publicfrontend.services.MatchService;
import com.utn.golmundial.publicfrontend.services.PredictionService;
import com.utn.golmundial.publicfrontend.services.PredictionServiceMock;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named
@ViewScoped
public class PredictionBean implements Serializable {

    private static final boolean usarMock = false;

    private final PredictionService predictionService = new PredictionService();
    private final PredictionServiceMock predictionServiceMock = new PredictionServiceMock();

    @Inject
    private MatchService matchService;

    // Usuario de prueba, configurable en app.properties (backend.usuarioId)
    private Long usuarioId = AppConfig.getUsuarioIdDemo();

    private List<Prediction> misApuestas;

    // Campos del formulario para crear una nueva apuesta
    private Long partidoId;
    private String pronostico;
    private Double monto;

    @PostConstruct
    public void init() {
        cargarApuestas();
    }

    public void cargarApuestas() {
        if (usarMock) {
            misApuestas = predictionServiceMock.obtenerApuestasUsuario(usuarioId);
        } else {
            misApuestas = predictionService.obtenerApuestasUsuario(usuarioId);
        }
    }

    public void apostar() {

        // Validación mínima antes de llamar a la API (evita mandar campos vacíos)
        if (partidoId == null || pronostico == null || pronostico.isBlank() || monto == null || monto <= 0) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Datos incompletos",
                            "Completa el ID del partido, el resultado y un monto mayor a 0."));
            return;
        }

        Match partido = matchService.findById(partidoId.intValue());
        if (partido == null) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Partido no encontrado",
                            "No existe ningún partido con ese ID en el calendario."));
            return;
        }

        Prediction nueva = new Prediction();
        nueva.setUsuarioId(usuarioId);
        nueva.setPartidoId(partidoId);
        nueva.setPronostico(pronostico);
        nueva.setMonto(monto);
        nueva.setFechaInicioPartido(partido.getKickoffUtc());

        Prediction resultado;
        if (usarMock) {
            resultado = predictionServiceMock.crearApuesta(nueva);
        } else {
            resultado = predictionService.crearApuesta(nueva);
        }

        if (resultado != null) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Éxito",
                            "Predicción creada correctamente."));
            cargarApuestas();

            // Limpia el formulario para la siguiente predicción
            partidoId = null;
            pronostico = null;
            monto = null;
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "No se pudo crear la predicción. Revisa la consola del servidor para ver el detalle (código HTTP y respuesta de la API)."));
        }
    }

    public List<Prediction> getMisApuestas() {
        return misApuestas;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public String getPronostico() {
        return pronostico;
    }

    public void setPronostico(String pronostico) {
        this.pronostico = pronostico;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }
}