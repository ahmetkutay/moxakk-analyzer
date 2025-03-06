package com.moxakk.analyzer.scraping.football.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for football scraping functionality.
 */
@Configuration
public class FootballScrapingConfig {

    /**
     * Creates and configures an ObjectMapper bean for JSON processing.
     *
     * @return A configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Creates and configures a RestTemplate bean for making HTTP requests specific to football scraping.
     *
     * @return A configured RestTemplate instance
     */
    @Bean
    public RestTemplate footballRestTemplate() {
        return new RestTemplate();
    }
}