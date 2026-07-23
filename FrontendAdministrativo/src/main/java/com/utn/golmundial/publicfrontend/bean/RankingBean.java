package com.utn.golmundial.publicfrontend.bean;

import com.utn.golmundial.publicfrontend.model.RankingEntry;
import com.utn.golmundial.publicfrontend.services.RankingService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class RankingBean implements Serializable {

    private final RankingService rankingService = new RankingService();

    private List<RankingEntry> ranking;

    @PostConstruct
    public void init() {

        ranking = rankingService.obtenerRanking();

    }

    public List<RankingEntry> getRanking() {
        return ranking;
    }

}