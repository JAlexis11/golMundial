package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.GroupStanding;
import java.util.List;
import java.util.Map;

public interface StandingsService {

    // Devuelve un mapa: "A" -> lista ordenada de posiciones del grupo A, etc.
    Map<String, List<GroupStanding>> computeGroupStandings();
}