package ec.utn.golmundial.estadisticas.service;

import ec.utn.golmundial.estadisticas.dto.GrupoDTO;
import ec.utn.golmundial.estadisticas.repository.GrupoRepository;
import ec.utn.golmundial.estadisticas.repository.SeleccionRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
public class GrupoService {

    @Inject
    private GrupoRepository grupoRepository;

    @Inject
    private SeleccionRepository seleccionRepository;

    public List<GrupoDTO> listar() {
        List<GrupoDTO> grupos = grupoRepository.listar();
        for (GrupoDTO grupo : grupos) {
            grupo.setSelecciones(seleccionRepository.listarResumenPorGrupo(grupo.getIdGrupo()));
        }
        return grupos;
    }
}
