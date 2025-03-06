package com.moxakk.analyzer.scraping.football.service;

import com.moxakk.analyzer.scraping.football.model.MatchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service for football scraping operations.
 * This class provides a high-level API for football scraping operations.
 */
@Service
public class FootballScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(FootballScrapingService.class);

    private final FootballMatchAnalyzer footballMatchAnalyzer;
    private final FootballCommentaryService footballCommentaryService;

    @Autowired
    public FootballScrapingService(FootballMatchAnalyzer footballMatchAnalyzer, FootballCommentaryService footballCommentaryService) {
        this.footballMatchAnalyzer = footballMatchAnalyzer;
        this.footballCommentaryService = footballCommentaryService;
    }

    /**
     * Analyzes a football match and generates commentary.
     *
     * @param homeTeam The home team
     * @param awayTeam The away team
     * @return A map containing match data and commentary
     */
    public Map<String, Object> analyzeMatchAndGenerateCommentary(String homeTeam, String awayTeam) {
        logger.info("Analyzing match and generating commentary for {} vs {}", homeTeam, awayTeam);

        // Analyze the match
        MatchData matchData = footballMatchAnalyzer.analyzeFootballMatch(homeTeam, awayTeam);

        // Generate commentary
        List<Object> commentary = footballCommentaryService.generateCommentary(matchData);

        // Return the results
        return Map.of(
            "matchData", matchData,
            "commentary", commentary
        );
    }
}