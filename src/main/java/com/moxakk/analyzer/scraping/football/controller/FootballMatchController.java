package com.moxakk.analyzer.scraping.football.controller;

import com.moxakk.analyzer.scraping.football.service.FootballScrapingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for football match analysis.
 * This controller provides endpoints for analyzing football matches.
 */
@RestController
@RequestMapping("/api")
public class FootballMatchController {

    private static final Logger logger = LoggerFactory.getLogger(FootballMatchController.class);

    private final FootballScrapingService footballScrapingService;

    @Autowired
    public FootballMatchController(FootballScrapingService footballScrapingService) {
        this.footballScrapingService = footballScrapingService;
    }

    /**
     * Analyzes a football match and generates commentary.
     *
     * @param request The match request containing home and away teams
     * @return A response entity containing match data and commentary
     */
    @PostMapping("/get-match")
    public ResponseEntity<Map<String, Object>> getMatch(@RequestBody MatchRequest request) {
        logger.info("Received match request: {}", request);

        try {
            // Validate request
            if (request.getHomeTeam() == null || request.getHomeTeam().isEmpty() ||
                request.getAwayTeam() == null || request.getAwayTeam().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Home team and away team are required"));
            }

            // Analyze match and generate commentary
            Map<String, Object> result = footballScrapingService.analyzeMatchAndGenerateCommentary(
                request.getHomeTeam(),
                request.getAwayTeam()
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error processing match request: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(createErrorResponse("Error processing match request: " + e.getMessage()));
        }
    }

    /**
     * Creates an error response.
     *
     * @param message The error message
     * @return A map containing the error message
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        return response;
    }

    /**
     * Request class for match analysis.
     */
    public static class MatchRequest {
        private String homeTeam;
        private String awayTeam;

        public MatchRequest() {
            // Default constructor
        }

        public MatchRequest(String homeTeam, String awayTeam) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
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

        @Override
        public String toString() {
            return "MatchRequest{" +
                    "homeTeam='" + homeTeam + '\'' +
                    ", awayTeam='" + awayTeam + '\'' +
                    '}';
        }
    }
}