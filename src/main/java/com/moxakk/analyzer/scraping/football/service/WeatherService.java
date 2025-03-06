package com.moxakk.analyzer.scraping.football.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxakk.analyzer.scraping.football.model.WeatherData;

/**
 * Service for fetching weather data for a venue.
 */
@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @Value("${openweather.api.key}")
    private String openWeatherApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherService(@Qualifier("footballRestTemplate") RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates default weather data when actual data cannot be retrieved.
     *
     * @return Default weather data
     */
    public WeatherData createDefaultWeatherData() {
        return new WeatherData(20, "Unknown", 50, 5);
    }

    /**
     * Gets weather data for a venue.
     *
     * @param venue The venue to get weather data for
     * @return Weather data for the venue
     */
    public WeatherData getWeatherData(String venue) {
        try {
            GeoLocation location = getGeocode(venue);
            if (location == null) {
                return createDefaultWeatherData();
            }

            return fetchWeather(location.getLat(), location.getLon());
        } catch (Exception e) {
            logger.error("Error fetching weather data: {}", e.getMessage(), e);
            return createDefaultWeatherData();
        }
    }

    /**
     * Gets geocode data for a venue.
     *
     * @param venue The venue to get geocode data for
     * @return Geocode data for the venue
     */
    private GeoLocation getGeocode(String venue) {
        try {
            String encodedVenue = URLEncoder.encode(venue, StandardCharsets.UTF_8);
            String geocodeUrl = "https://nominatim.openstreetmap.org/search?q=" + encodedVenue + "&format=json&limit=1";

            String response = restTemplate.getForObject(geocodeUrl, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.isEmpty() || !jsonNode.get(0).has("lat") || !jsonNode.get(0).has("lon")) {
                return null;
            }

            return new GeoLocation(
                jsonNode.get(0).get("lat").asText(),
                jsonNode.get(0).get("lon").asText()
            );
        } catch (Exception e) {
            logger.error("Error getting geocode data: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Fetches weather data for a location.
     *
     * @param lat Latitude of the location
     * @param lon Longitude of the location
     * @return Weather data for the location
     */
    private WeatherData fetchWeather(String lat, String lon) {
        try {
            String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon +
                         "&appid=" + openWeatherApiKey + "&units=metric";

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            double temperature = jsonNode.get("main").get("temp").asDouble();
            String condition = jsonNode.get("weather").get(0).get("description").asText();
            int humidity = jsonNode.get("main").get("humidity").asInt();
            double windSpeed = jsonNode.get("wind").get("speed").asDouble();

            return new WeatherData(temperature, condition, humidity, windSpeed);
        } catch (Exception e) {
            logger.error("Error fetching weather data: {}", e.getMessage(), e);
            return createDefaultWeatherData();
        }
    }

    /**
     * Inner class to represent geocode location data.
     */
    private static class GeoLocation {
        private final String lat;
        private final String lon;

        public GeoLocation(String lat, String lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public String getLat() {
            return lat;
        }

        public String getLon() {
            return lon;
        }
    }
}