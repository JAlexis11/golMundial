package com.utn.golmundial.publicfrontend.model;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import java.io.Serializable;

public class Match implements Serializable {

    private Integer id;

    @JsonbProperty("numeroPartidoFifa")
    private Integer fifaMatchNumber;

    @JsonbProperty("fase")
    private String phase;

    @JsonbProperty("grupo")
    private String group;

    @JsonbProperty("seleccionLocal")
    private String homeTeam;

    @JsonbProperty("seleccionVisitante")
    private String awayTeam;

    @JsonbProperty("fechaHoraUtc")
    private String kickoffUtc;

    @JsonbProperty("sedeId")
    private Integer venueId;

    @JsonbProperty("estado")
    private String status;

    @JsonbProperty("golesLocal")
    private Integer homeGoals;

    @JsonbProperty("golesVisitante")
    private Integer awayGoals;

    @JsonbTransient
    private String venueLabel;

    @JsonbTransient
    private String displayDate;

    public Integer getId() { 
        return id; 
    }
    public void setId(Integer id) { 
        this.id = id; 
    }
    public Integer getFifaMatchNumber() { 
        return fifaMatchNumber; 
    }
    public void setFifaMatchNumber(Integer fifaMatchNumber) { 
        this.fifaMatchNumber = fifaMatchNumber; 
    }
    public String getPhase() { 
        return phase; 
    }
    public void setPhase(String phase) { 
        this.phase = phase; 
    }
    public String getGroup() { 
        return group; 
    }
    public void setGroup(String group) { 
        this.group = group; 
    }
    public String getHomeTeam() { 
        return homeTeam; 
    }
    public void setHomeTeam(String homeTeam) { 
        this.homeTeam = homeTeam; 
    }
    public String getAwayTeam() { 
        return awayTeam; 
    }
    public void setAwayTeam(String awayTeam) { 
        this.awayTeam = awayTeam; 
    }
    public String getKickoffUtc() { 
        return kickoffUtc; 
    }
    public void setKickoffUtc(String kickoffUtc) { 
        this.kickoffUtc = kickoffUtc; 
    }
    public Integer getVenueId() { 
        return venueId;
    }
    public void setVenueId(Integer venueId) { 
        this.venueId = venueId; 
    }
    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }
    public Integer getHomeGoals() { 
        return homeGoals; 
    }
    public void setHomeGoals(Integer homeGoals) { 
        this.homeGoals = homeGoals; 
    }
    public Integer getAwayGoals() { 
        return awayGoals; 
    }
    public void setAwayGoals(Integer awayGoals) { 
        this.awayGoals = awayGoals; 
    }
    public String getVenueLabel() { 
        return venueLabel; 
    }
    public void setVenueLabel(String venueLabel) { 
        this.venueLabel = venueLabel; 
    }
    public String getDisplayDate() { 
        return displayDate; 
    }
    public void setDisplayDate(String displayDate) { 
        this.displayDate = displayDate; 
    }
}