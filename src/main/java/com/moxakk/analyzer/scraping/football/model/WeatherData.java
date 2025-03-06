package com.moxakk.analyzer.scraping.football.model;

/**
 * Represents weather data for a football match.
 */
public class WeatherData {
    private double temperature;
    private String condition;
    private int humidity;
    private double windSpeed;

    public WeatherData() {
        // Default constructor
    }

    public WeatherData(double temperature, String condition, int humidity, double windSpeed) {
        this.temperature = temperature;
        this.condition = condition;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}