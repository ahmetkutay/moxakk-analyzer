package com.moxakk.analyzer.scraping.football.service;

import com.moxakk.analyzer.scraping.football.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for generating commentary for football matches.
 */
@Service
public class FootballCommentaryService {

    private static final Logger logger = LoggerFactory.getLogger(FootballCommentaryService.class);

    private final AIService aiService;

    @Autowired
    public FootballCommentaryService(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * Generates commentary for a football match.
     *
     * @param data The match data
     * @return List of commentary responses from different AI providers
     */
    public List<Object> generateCommentary(MatchData data) {
        String prompt = generatePrompt(data);

        try {
            List<Object> responses = new ArrayList<>();

            // Get responses from different AI providers
            responses.add(aiService.getGeminiResponse(prompt));
            responses.add(aiService.getOpenAIResponse(prompt));
            responses.add(aiService.getCohereResponse(prompt));
            responses.add(aiService.getAnthropicResponse(prompt));
            responses.add(aiService.getMistralResponse(prompt));

            return responses;
        } catch (Exception e) {
            logger.error("Error generating commentary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate commentary: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a prompt for the AI model based on match data.
     *
     * @param data The match data
     * @return Prompt for the AI model
     */
    private String generatePrompt(MatchData data) {
        StringBuilder prompt = new StringBuilder();

        // Format unavailable players
        String homePlayerAvailabilityList = formatList(data.getUnavailablePlayers().get("home"));
        String awayPlayerAvailabilityList = formatList(data.getUnavailablePlayers().get("away"));

        // Format recent matches
        String homeMatchResults = formatList(data.getRecentMatches().get("home"));
        String awayMatchResults = formatList(data.getRecentMatches().get("away"));
        String betweenMatchResults = formatList(data.getRecentMatches().get("between"));

        // Get standings
        TeamStanding homeTeamStanding = data.getStandings().getHome();
        TeamStanding awayTeamStanding = data.getStandings().getAway();

        // Format lineup players
        String homeLineup = formatLineup(data.getTeamLineups().getHome().getPlayers());
        String awayLineup = formatLineup(data.getTeamLineups().getAway().getPlayers());

        // Get formations
        String homeFormation = data.getTeamLineups().getHome().getFormation() != null ?
                data.getTeamLineups().getHome().getFormation() : "Unknown";
        String awayFormation = data.getTeamLineups().getAway().getFormation() != null ?
                data.getTeamLineups().getAway().getFormation() : "Unknown";

        prompt.append("You are an expert football analyst and prediction model. Based on the provided match data, generate a detailed predictive analysis.\n\n");

        prompt.append("Match Information:\n");
        prompt.append("- ID: ").append(data.getId()).append("\n");
        prompt.append("- Teams: ").append(data.getHomeTeam()).append(" vs ").append(data.getAwayTeam()).append("\n\n");

        prompt.append("Team Formations and Lineups:\n");
        prompt.append(data.getHomeTeam()).append(" (").append(homeFormation).append("):\n");
        prompt.append(homeLineup).append("\n\n");

        prompt.append(data.getAwayTeam()).append(" (").append(awayFormation).append("):\n");
        prompt.append(awayLineup).append("\n\n");

        prompt.append("Standings:\n");
        prompt.append(data.getHomeTeam()).append(" Standings:\n");
        prompt.append("- Position: ").append(homeTeamStanding.getOverall().getPosition()).append("\n");
        prompt.append("- Played: ").append(homeTeamStanding.getOverall().getPlayed()).append("\n");
        prompt.append("- Won: ").append(homeTeamStanding.getOverall().getWon()).append("\n");
        prompt.append("- Drawn: ").append(homeTeamStanding.getOverall().getDrawn()).append("\n");
        prompt.append("- Lost: ").append(homeTeamStanding.getOverall().getLost()).append("\n");
        prompt.append("- Goals For: ").append(homeTeamStanding.getOverall().getGoalsFor()).append("\n");
        prompt.append("- Goals Against: ").append(homeTeamStanding.getOverall().getGoalsAgainst()).append("\n");
        prompt.append("- Goal Difference: ").append(homeTeamStanding.getOverall().getGoalDifference()).append("\n");
        prompt.append("- Points: ").append(homeTeamStanding.getOverall().getPoints()).append("\n\n");

        prompt.append(data.getHomeTeam()).append(" Home Standings:\n");
        prompt.append("- Position: ").append(homeTeamStanding.getHomeForm().getPosition()).append("\n");
        prompt.append("- Played: ").append(homeTeamStanding.getHomeForm().getPlayed()).append("\n");
        prompt.append("- Won: ").append(homeTeamStanding.getHomeForm().getWon()).append("\n");
        prompt.append("- Drawn: ").append(homeTeamStanding.getHomeForm().getDrawn()).append("\n");
        prompt.append("- Lost: ").append(homeTeamStanding.getHomeForm().getLost()).append("\n");
        prompt.append("- Goals For: ").append(homeTeamStanding.getHomeForm().getGoalsFor()).append("\n");
        prompt.append("- Goals Against: ").append(homeTeamStanding.getHomeForm().getGoalsAgainst()).append("\n");
        prompt.append("- Goal Difference: ").append(homeTeamStanding.getHomeForm().getGoalDifference()).append("\n\n");

        prompt.append(data.getHomeTeam()).append(" Away Standings:\n");
        prompt.append("- Position: ").append(homeTeamStanding.getAwayForm().getPosition()).append("\n");
        prompt.append("- Played: ").append(homeTeamStanding.getAwayForm().getPlayed()).append("\n");
        prompt.append("- Won: ").append(homeTeamStanding.getAwayForm().getWon()).append("\n");
        prompt.append("- Drawn: ").append(homeTeamStanding.getAwayForm().getDrawn()).append("\n");
        prompt.append("- Lost: ").append(homeTeamStanding.getAwayForm().getLost()).append("\n");
        prompt.append("- Goals For: ").append(homeTeamStanding.getAwayForm().getGoalsFor()).append("\n");
        prompt.append("- Goals Against: ").append(homeTeamStanding.getAwayForm().getGoalsAgainst()).append("\n");
        prompt.append("- Goal Difference: ").append(homeTeamStanding.getAwayForm().getGoalDifference()).append("\n\n");

        prompt.append(data.getAwayTeam()).append(" Standings:\n");
        prompt.append("- Position: ").append(awayTeamStanding.getOverall().getPosition()).append("\n");
        prompt.append("- Played: ").append(awayTeamStanding.getOverall().getPlayed()).append("\n");
        prompt.append("- Won: ").append(awayTeamStanding.getOverall().getWon()).append("\n");
        prompt.append("- Drawn: ").append(awayTeamStanding.getOverall().getDrawn()).append("\n");
        prompt.append("- Lost: ").append(awayTeamStanding.getOverall().getLost()).append("\n");
        prompt.append("- Goals For: ").append(awayTeamStanding.getOverall().getGoalsFor()).append("\n");
        prompt.append("- Goals Against: ").append(awayTeamStanding.getOverall().getGoalsAgainst()).append("\n");
        prompt.append("- Goal Difference: ").append(awayTeamStanding.getOverall().getGoalDifference()).append("\n");
        prompt.append("- Points: ").append(awayTeamStanding.getOverall().getPoints()).append("\n\n");

        prompt.append(data.getAwayTeam()).append(" Home Standings:\n");
        prompt.append("- Position: ").append(awayTeamStanding.getHomeForm().getPosition()).append("\n");
        prompt.append("- Played: ").append(awayTeamStanding.getHomeForm().getPlayed()).append("\n");
        prompt.append("- Won: ").append(awayTeamStanding.getHomeForm().getWon()).append("\n");
        prompt.append("- Drawn: ").append(awayTeamStanding.getHomeForm().getDrawn()).append("\n");
        prompt.append("- Lost: ").append(awayTeamStanding.getHomeForm().getLost()).append("\n");
        prompt.append("- Goals For: ").append(awayTeamStanding.getHomeForm().getGoalsFor()).append("\n");
        prompt.append("- Goals Against: ").append(awayTeamStanding.getHomeForm().getGoalsAgainst()).append("\n");
        prompt.append("- Goal Difference: ").append(awayTeamStanding.getHomeForm().getGoalDifference()).append("\n\n");

        prompt.append(data.getAwayTeam()).append(" Away Standings:\n");
        prompt.append("- Position: ").append(awayTeamStanding.getAwayForm().getPosition()).append("\n");
        prompt.append("- Played: ").append(awayTeamStanding.getAwayForm().getPlayed()).append("\n");
        prompt.append("- Won: ").append(awayTeamStanding.getAwayForm().getWon()).append("\n");
        prompt.append("- Drawn: ").append(awayTeamStanding.getAwayForm().getDrawn()).append("\n");
        prompt.append("- Lost: ").append(awayTeamStanding.getAwayForm().getLost()).append("\n");
        prompt.append("- Goals For: ").append(awayTeamStanding.getAwayForm().getGoalsFor()).append("\n");
        prompt.append("- Goals Against: ").append(awayTeamStanding.getAwayForm().getGoalsAgainst()).append("\n");
        prompt.append("- Goal Difference: ").append(awayTeamStanding.getAwayForm().getGoalDifference()).append("\n\n");

        prompt.append("Environmental Conditions:\n");
        prompt.append("- Temperature: ").append(data.getWeather().getTemperature()).append("°C\n");
        prompt.append("- Weather: ").append(data.getWeather().getCondition()).append("\n");
        prompt.append("- Humidity: ").append(data.getWeather().getHumidity()).append("%\n");
        prompt.append("- Wind Speed: ").append(data.getWeather().getWindSpeed()).append(" km/h\n\n");

        prompt.append("Team Form Analysis:\n");
        prompt.append(data.getHomeTeam()).append(" Recent Form:\n");
        prompt.append(homeMatchResults).append("\n\n");

        prompt.append(data.getAwayTeam()).append(" Recent Form:\n");
        prompt.append(awayMatchResults).append("\n\n");

        prompt.append("Head-to-Head History:\n");
        prompt.append(betweenMatchResults).append("\n\n");

        prompt.append("Squad Status:\n");
        prompt.append(data.getHomeTeam()).append(" Unavailable Players:\n");
        prompt.append(homePlayerAvailabilityList.isEmpty() ? "No reported absences" : homePlayerAvailabilityList).append("\n\n");

        prompt.append(data.getAwayTeam()).append(" Unavailable Players:\n");
        prompt.append(awayPlayerAvailabilityList.isEmpty() ? "No reported absences" : awayPlayerAvailabilityList).append("\n\n");

        prompt.append(getPromptRequirements());

        logger.info("Generated prompt: {}", prompt);

        return prompt.toString();
    }

    /**
     * Formats a list of strings into a single string with each item on a new line.
     *
     * @param list The list to format
     * @return Formatted string
     */
    private String formatList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        return String.join("\n", list);
    }

    /**
     * Formats a list of player positions into a single string with each player on a new line.
     *
     * @param players The list of player positions to format
     * @return Formatted string
     */
    private String formatLineup(List<PlayerPosition> players) {
        if (players == null || players.isEmpty()) {
            return "No lineup available";
        }

        StringBuilder lineup = new StringBuilder();

        for (PlayerPosition player : players) {
            lineup.append(player.getNumber() != null ? player.getNumber() : "?")
                  .append(". ")
                  .append(player.getName() != null ? player.getName() : "Unknown")
                  .append(" (")
                  .append(player.getPosition() != null ? player.getPosition() : "Unknown")
                  .append(")\n");
        }

        return lineup.toString();
    }

    /**
     * Gets the prompt requirements for the AI model.
     *
     * @return Prompt requirements
     */
    private String getPromptRequirements() {
        return "Simulate the match based on the data provided and generate a detailed predictive analysis.\n" +
               "Analyze all provided data and respond with a single JSON object in exactly this format:\n" +
               "{\n" +
               "    \"homeTeamWinPercentage\": number,     // Probability of home team victory (0-100)\n" +
               "    \"awayTeamWinPercentage\": number,     // Probability of away team victory (0-100)\n" +
               "    \"drawPercentage\": number,            // Probability of a draw (0-100)\n" +
               "    \"over2_5Percentage\": number,         // Likelihood of over 2.5 goals\n" +
               "    \"bothTeamScorePercentage\": number,   // Probability of both teams scoring\n" +
               "    \"halfTimeWinner\": \"home\" | \"away\" | \"draw\",  // Predicted half-time result\n" +
               "    \"halfTimeWinnerPercentage\": number,  // Confidence in half-time prediction\n" +
               "    \"predictedScore\": {\n" +
               "        \"home\": number,                  // Predicted goals for home team\n" +
               "        \"away\": number                   // Predicted goals for away team\n" +
               "    },\n" +
               "    \"predictionConfidence\": number,      // Overall confidence in prediction\n" +
               "    \"briefComment\": string              // Analytical comment explaining key factors and prediction rationale\n" +
               "}\n\n" +
               "Critical Requirements:\n" +
               "1. All percentages must be numbers from 0 to 100\n" +
               "2. Win percentages (home, away, draw) must sum exactly to 100\n" +
               "3. Brief comment should explain the prediction rationale considering team strengths and formations\n" +
               "4. Predication confidence should reflect:\n" +
               "   - Data completeness\n" +
               "   - Form consistency\n" +
               "   - Weather impact\n" +
               "   - Squad availability\n" +
               "   - Starting lineup quality\n" +
               "   - Tactical matchup (formations)\n" +
               "5. Consider:\n" +
               "   - Team formations and player positions\n" +
               "   - Individual player matchups\n" +
               "   - Recent form and consistency\n" +
               "   - Head-to-head history\n" +
               "   - Weather conditions impact\n" +
               "   - Available players and team strength\n" +
               "   - Home/away advantage\n\n" +
               "Return ONLY the JSON object without any additional text or formatting.";
    }
}