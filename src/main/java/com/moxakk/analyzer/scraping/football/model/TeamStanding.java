package com.moxakk.analyzer.scraping.football.model;

/**
 * Represents a team's standing in different contexts (overall, home, away).
 */
public class TeamStanding {
    private TeamStandingData overall;
    private TeamStandingData homeForm;
    private TeamStandingData awayForm;

    public TeamStanding() {
        // Default constructor
    }

    public TeamStanding(TeamStandingData overall, TeamStandingData homeForm, TeamStandingData awayForm) {
        this.overall = overall;
        this.homeForm = homeForm;
        this.awayForm = awayForm;
    }

    public TeamStandingData getOverall() {
        return overall;
    }

    public void setOverall(TeamStandingData overall) {
        this.overall = overall;
    }

    public TeamStandingData getHomeForm() {
        return homeForm;
    }

    public void setHomeForm(TeamStandingData homeForm) {
        this.homeForm = homeForm;
    }

    public TeamStandingData getAwayForm() {
        return awayForm;
    }

    public void setAwayForm(TeamStandingData awayForm) {
        this.awayForm = awayForm;
    }
}