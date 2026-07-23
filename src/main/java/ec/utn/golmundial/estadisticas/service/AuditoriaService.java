package ec.utn.golmundial.estadisticas.service;

import ec.utn.golmundial.estadisticas.entity.Auditoria;
import ec.utn.golmundial.estadisticas.repository.AuditoriaRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;

@Stateless
public class AuditoriaService {

    @Inject
    private AuditoriaRepository repo;

    public void registrar(String tablaAfectada, Integer idRegistro, String accion, Integer idUsuarioActor) {
        Auditoria auditoria = new Auditoria();
        auditoria.setTablaAfectada(tablaAfectada);
        auditoria.setIdRegistro(idRegistro);
        auditoria.setAccion(accion);
        auditoria.setFecha(LocalDateTime.now());
        if (idUsuarioActor != null) {
            auditoria.setUsuarioActor(repo.obtenerReferenciaUsuario(idUsuarioActor));
        }
        repo.registrar(auditoria);
    }
}
