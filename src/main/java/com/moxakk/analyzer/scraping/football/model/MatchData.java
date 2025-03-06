package com.moxakk.analyzer.scraping.football.model;

import java.util.List;
import java.util.Map;

/**
 * Represents the data for a football match.
 */
public class MatchData {
    private String id;
    private String matchInput;
    private String homeTeam;
    private String awayTeam;
    private String venue;
    private Map<String, List<String>> unavailablePlayers;
    private Map<String, List<String>> recentMatches;
    private WeatherData weather;
    private TeamLineups teamLineups;
    private StandingsResult standings;

    public MatchData() {
        // Default constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatchInput() {
        return matchInput;
    }

    public void setMatchInput(String matchInput) {
        this.matchInput = matchInput;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Map<String, List<String>> getUnavailablePlayers() {
        return unavailablePlayers;
    }

    public void setUnavailablePlayers(Map<String, List<String>> unavailablePlayers) {
        this.unavailablePlayers = unavailablePlayers;
    }

    public Map<String, List<String>> getRecentMatches() {
        return recentMatches;
    }

    public void setRecentMatches(Map<String, List<String>> recentMatches) {
        this.recentMatches = recentMatches;
    }

    public WeatherData getWeather() {
        return weather;
    }

    public void setWeather(WeatherData weather) {
        this.weather = weather;
    }

    public TeamLineups getTeamLineups() {
        return teamLineups;
    }

    public void setTeamLineups(TeamLineups teamLineups) {
        this.teamLineups = teamLineups;
    }

    public StandingsResult getStandings() {
        return standings;
    }

    public void setStandings(StandingsResult standings) {
        this.standings = standings;
    }
}