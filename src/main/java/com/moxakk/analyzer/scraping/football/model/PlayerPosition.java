package com.moxakk.analyzer.scraping.football.model;

/**
 * Represents a player's position in a football match.
 */
public class PlayerPosition {
    private Integer number;
    private String name;
    private String position;

    public PlayerPosition() {
        // Default constructor
    }

    public PlayerPosition(Integer number, String name, String position) {
        this.number = number;
        this.name = name;
        this.position = position;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}