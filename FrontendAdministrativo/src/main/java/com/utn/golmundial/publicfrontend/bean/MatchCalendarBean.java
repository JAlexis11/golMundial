package com.utn.golmundial.publicfrontend.bean;

import com.utn.golmundial.publicfrontend.model.CountryUtil;
import com.utn.golmundial.publicfrontend.model.Match;
import com.utn.golmundial.publicfrontend.services.MatchService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class MatchCalendarBean implements Serializable {

    @Inject
    private MatchService matchService;

    private List<Match> matches;

    @PostConstruct
    public void init() {
        matches = matchService.listGroupStageMatches();
    }

    public List<Match> getMatches() {
        return matches;
    }

    public List<String> getGroupKeys() {
        return matches.stream()
                .map(Match::getGroup)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Match> getMatchesByGroup(String group) {
        return matches.stream()
                .filter(m -> group.equals(m.getGroup()))
                .sorted(Comparator.comparing(
                        Match::getKickoffUtc,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    // ==========================
    // BANDERA
    // ==========================
    public String getFlag(String code) {
        return CountryUtil.getFlag(code);
    }

    // ==========================
    // NOMBRE DEL PAÍS
    // ==========================
    public String getCountry(String code) {
        return CountryUtil.getName(code);
    }

}