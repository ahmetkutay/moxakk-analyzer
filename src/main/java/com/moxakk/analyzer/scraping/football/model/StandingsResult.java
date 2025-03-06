package com.moxakk.analyzer.scraping.football.model;

/**
 * Represents the standings of both teams in a football match.
 */
public class StandingsResult {
    private TeamStanding home;
    private TeamStanding away;

    public StandingsResult() {
        // Default constructor
    }

    public StandingsResult(TeamStanding home, TeamStanding away) {
        this.home = home;
        this.away = away;
    }

    public TeamStanding getHome() {
        return home;
    }

    public void setHome(TeamStanding home) {
        this.home = home;
    }

    public TeamStanding getAway() {
        return away;
    }

    public void setAway(TeamStanding away) {
        this.away = away;
    }
}