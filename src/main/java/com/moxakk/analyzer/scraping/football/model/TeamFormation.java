package com.moxakk.analyzer.scraping.football.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a team's formation in a football match.
 */
public class TeamFormation {
    private String formation;
    private List<PlayerPosition> players;

    public TeamFormation() {
        this.players = new ArrayList<>();
    }

    public TeamFormation(String formation, List<PlayerPosition> players) {
        this.formation = formation;
        this.players = players != null ? players : new ArrayList<>();
    }

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public List<PlayerPosition> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerPosition> players) {
        this.players = players;
    }

    public void addPlayer(PlayerPosition player) {
        if (this.players == null) {
            this.players = new ArrayList<>();
        }
        this.players.add(player);
    }
}