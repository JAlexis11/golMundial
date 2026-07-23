package com.utn.golmundial.publicfrontend.bean;

import com.utn.golmundial.publicfrontend.model.GroupStanding;
import com.utn.golmundial.publicfrontend.services.StandingsService;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class StandingsBean implements Serializable {

    @Inject
    private StandingsService standingsService;

    private Map<String, List<GroupStanding>> standingsByGroup;

    @PostConstruct
    public void init() {
        standingsByGroup = standingsService.computeGroupStandings();
    }

    // Lista de nombres de grupo ("A", "B", "C"...) ya ordenada,
    // para que la página sepa cuántas pestañas/tablas dibujar.
    public List<String> getGroupKeys() {
        return new java.util.ArrayList<>(new TreeSet<>(standingsByGroup.keySet()));
    }

    public Map<String, List<GroupStanding>> getStandingsByGroup() {
        return standingsByGroup;
    }
    public List<GroupStanding> getSortedStandings(String groupKey) {
    List<GroupStanding> lista = standingsByGroup.get(groupKey);
    if (lista == null) {
        return List.of();
    }
    return lista.stream()
            .sorted(
                Comparator.comparingInt(GroupStanding::getPoints).reversed()
                    .thenComparing(Comparator.comparingInt(GroupStanding::getGoalDifference).reversed())
                    .thenComparing(Comparator.comparingInt(GroupStanding::getGoalsFor).reversed())
            )
            .collect(Collectors.toList());
}
}