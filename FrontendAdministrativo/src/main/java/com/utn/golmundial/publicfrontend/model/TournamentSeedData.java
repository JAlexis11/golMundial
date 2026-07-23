package com.utn.golmundial.publicfrontend.model;

import jakarta.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.List;

public class TournamentSeedData implements Serializable {

    @JsonbProperty("partidos")
    private List<Match> matches;

    @JsonbProperty("sedes")
    private List<Venue> venues;

    public List<Match> getMatches() { 
        return matches; 
    }
    public void setMatches(List<Match> matches) { 
        this.matches = matches; 
    }
    public List<Venue> getVenues() { 
        return venues; 
    }
    public void setVenues(List<Venue> venues) { 
        this.venues = venues; 
    }
}