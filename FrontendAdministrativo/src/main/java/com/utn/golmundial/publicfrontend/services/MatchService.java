package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.Match;
import java.util.List;

public interface MatchService {

    List<Match> listGroupStageMatches();

    Match findById(Integer id);
}