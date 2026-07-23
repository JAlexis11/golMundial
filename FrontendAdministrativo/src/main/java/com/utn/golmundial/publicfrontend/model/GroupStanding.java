package com.utn.golmundial.publicfrontend.model;

import java.io.Serializable;

public class GroupStanding implements Serializable {

    private String team;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int goalsFor;
    private int goalsAgainst;

    public GroupStanding(String team) {
        this.team = team;
    }
    // Se llama una vez por cada partido donde este equipo participó,
    // sumando los datos poco a poco.
    public void registerMatch(int goalsScored, int goalsConceded) {
        played++;
        goalsFor += goalsScored;
        goalsAgainst += goalsConceded;

        if (goalsScored > goalsConceded) {
            won++;
        } else if (goalsScored == goalsConceded) {
            drawn++;
        } else {
            lost++;
        }
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    // 3 puntos por victoria, 1 por empate: regla est\u00e1ndar de f\u00fatbol.
    public int getPoints() {
        return (won * 3) + drawn;
    }

    public String getTeam() { 
        return team; 
    }
    public int getPlayed() {
        return played; 
    }
    public int getWon() { 
        return won; 
    }
    public int getDrawn() { 
        return drawn; 
    }
    public int getLost() { 
        return lost; 
    }
    public int getGoalsFor() { 
        return goalsFor; 
    }
    public int getGoalsAgainst() { 
        return goalsAgainst; 
    }
}