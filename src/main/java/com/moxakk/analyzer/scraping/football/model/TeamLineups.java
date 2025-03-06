package com.moxakk.analyzer.scraping.football.model;

/**
 * Represents the lineups of both teams in a football match.
 */
public class TeamLineups {
    private TeamFormation home;
    private TeamFormation away;

    public TeamLineups() {
        this.home = new TeamFormation();
        this.away = new TeamFormation();
    }

    public TeamLineups(TeamFormation home, TeamFormation away) {
        this.home = home != null ? home : new TeamFormation();
        this.away = away != null ? away : new TeamFormation();
    }

    public TeamFormation getHome() {
        return home;
    }

    public void setHome(TeamFormation home) {
        this.home = home;
    }

    public TeamFormation getAway() {
        return away;
    }

    public void setAway(TeamFormation away) {
        this.away = away;
    }
}