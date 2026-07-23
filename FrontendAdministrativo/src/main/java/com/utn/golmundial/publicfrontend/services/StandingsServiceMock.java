package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.GroupStanding;
import com.utn.golmundial.publicfrontend.model.Match;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;

@ApplicationScoped
public class StandingsServiceMock implements StandingsService {

    @Inject
    private MatchService matchService;

    private Map<String, List<GroupStanding>> standingsByGroup;

    // TODO: replace with a REST call to Alexis's Statistics backend (RF06)
    // once it's ready. That backend already calculates this automatically
    // every time a real result is registered.
    @PostConstruct
    public void computeOnStartup() {
        standingsByGroup = new TreeMap<>(); // TreeMap = se ordena solo alfabéticamente (A, B, C...)
        Map<String, Map<String, GroupStanding>> teamsByGroup = new HashMap<>();

        for (Match match : matchService.listGroupStageMatches()) {

            // Genera un resultado simulado, pero SIEMPRE el mismo para el
            // mismo partido (usamos el id del partido como "semilla"),
            // para que la tabla no cambie cada vez que recargas la página.
            Random random = new Random(match.getId());
            int homeGoals = random.nextInt(4); // 0 a 3 goles
            int awayGoals = random.nextInt(4);

            String group = match.getGroup();
            teamsByGroup.putIfAbsent(group, new HashMap<>());
            Map<String, GroupStanding> teams = teamsByGroup.get(group);

            teams.putIfAbsent(match.getHomeTeam(), new GroupStanding(match.getHomeTeam()));
            teams.putIfAbsent(match.getAwayTeam(), new GroupStanding(match.getAwayTeam()));

            teams.get(match.getHomeTeam()).registerMatch(homeGoals, awayGoals);
            teams.get(match.getAwayTeam()).registerMatch(awayGoals, homeGoals);
        }

        // Ordena cada grupo por puntos, luego diferencia de gol, luego goles a favor.
        for (Map.Entry<String, Map<String, GroupStanding>> entry : teamsByGroup.entrySet()) {
            List<GroupStanding> sorted = new ArrayList<>(entry.getValue().values());
            sorted.sort(Comparator
                    .comparingInt(GroupStanding::getPoints).reversed()
                    .thenComparing(Comparator.comparingInt(GroupStanding::getGoalDifference).reversed())
                    .thenComparing(Comparator.comparingInt(GroupStanding::getGoalsFor).reversed()));
            standingsByGroup.put(entry.getKey(), sorted);
        }
    }

    @Override
    public Map<String, List<GroupStanding>> computeGroupStandings() {
        return standingsByGroup;
    }
}