package com.moxakk.analyzer.scraping.football.service;

import com.moxakk.analyzer.scraping.football.exception.ScrapingError;
import com.moxakk.analyzer.scraping.football.model.*;
import com.moxakk.analyzer.scraping.football.util.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for analyzing football matches by scraping data from websites.
 */
@Service
public class FootballMatchAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(FootballMatchAnalyzer.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    private final WeatherService weatherService;
    private final JdbcTemplate jdbcTemplate;
    private final WebDriverFactory webDriverFactory;

    @Autowired
    public FootballMatchAnalyzer(WeatherService weatherService, JdbcTemplate jdbcTemplate, WebDriverFactory webDriverFactory) {
        this.weatherService = weatherService;
        this.jdbcTemplate = jdbcTemplate;
        this.webDriverFactory = webDriverFactory;
    }

    /**
     * Analyzes a football match by scraping data from websites.
     *
     * @param homeTeam The home team
     * @param awayTeam The away team
     * @return Match data
     */
    public MatchData analyzeFootballMatch(String homeTeam, String awayTeam) {
        String matchInput = homeTeam + "-" + awayTeam;

        // Check if the match data already exists in the database
        try {
            List<Map<String, Object>> existingMatches = jdbcTemplate.queryForList(
                    "SELECT * FROM match_data WHERE id = ?",
                    matchInput
            );

            if (!existingMatches.isEmpty()) {
                logger.info("Match data found in database for {}", matchInput);
                // Convert the database record to MatchData
                // This is a simplified version, you would need to implement the actual conversion
                return convertDatabaseRecordToMatchData(existingMatches.get(0));
            }
        } catch (Exception e) {
            logger.error("Error checking database for match data: {}", e.getMessage(), e);
            // Continue with scraping if database check fails
        }

        WebDriver driver = null;
        try {
            driver = webDriverFactory.createWebDriver();

            // Initialize match data
            MatchData matchData = new MatchData();
            matchData.setId(matchInput);
            matchData.setMatchInput(matchInput);
            matchData.setHomeTeam(homeTeam);
            matchData.setAwayTeam(awayTeam);

            // Get venue
            String venue = scrapeVenue(driver, homeTeam, awayTeam);
            matchData.setVenue(venue);

            // Get weather data
            WeatherData weatherData = weatherService.getWeatherData(venue);
            matchData.setWeather(weatherData);

            // Get unavailable players
            Map<String, List<String>> unavailablePlayers = scrapeUnavailablePlayers(driver, homeTeam, awayTeam);
            matchData.setUnavailablePlayers(unavailablePlayers);

            // Get recent matches
            Map<String, List<String>> recentMatches = scrapeRecentMatches(driver, homeTeam, awayTeam);
            matchData.setRecentMatches(recentMatches);

            // Get team lineups
            TeamLineups teamLineups = scrapeTeamLineups(driver, homeTeam, awayTeam);
            matchData.setTeamLineups(teamLineups);

            // Get standings
            StandingsResult standings = scrapeStandings(driver, homeTeam, awayTeam);
            matchData.setStandings(standings);

            // Save match data to database
            saveMatchDataToDatabase(matchData);

            return matchData;
        } catch (Exception e) {
            logger.error("Error analyzing football match: {}", e.getMessage(), e);
            throw new ScrapingError("Failed to analyze football match: " + e.getMessage(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * Scrapes the venue for a match.
     *
     * @param driver WebDriver instance
     * @param homeTeam The home team
     * @param awayTeam The away team
     * @return The venue
     */
    private String scrapeVenue(WebDriver driver, String homeTeam, String awayTeam) {
        try {
            // This is a placeholder implementation
            // You would need to implement the actual scraping logic
            String url = "https://www.example.com/matches/" + homeTeam.toLowerCase() + "-vs-" + awayTeam.toLowerCase();
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement venueElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".venue-info")));

            return venueElement.getText();
        } catch (Exception e) {
            logger.error("Error scraping venue: {}", e.getMessage(), e);
            return homeTeam + " Stadium"; // Default venue
        }
    }

    /**
     * Scrapes unavailable players for both teams.
     *
     * @param driver WebDriver instance
     * @param homeTeam The home team
     * @param awayTeam The away team
     * @return Map of unavailable players for both teams
     */
    private Map<String, List<String>> scrapeUnavailablePlayers(WebDriver driver, String homeTeam, String awayTeam) {
        Map<String, List<String>> unavailablePlayers = new HashMap<>();
        unavailablePlayers.put("home", new ArrayList<>());
        unavailablePlayers.put("away", new ArrayList<>());

        try {
            // This is a placeholder implementation
            // You would need to implement the actual scraping logic
            String url = "https://www.example.com/team-news/" + homeTeam.toLowerCase() + "-vs-" + awayTeam.toLowerCase();
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> homeUnavailableElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".home-team .unavailable-players li")));

            List<WebElement> awayUnavailableElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".away-team .unavailable-players li")));

            unavailablePlayers.put("home", homeUnavailableElements.stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList()));

            unavailablePlayers.put("away", awayUnavailableElements.stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("Error scraping unavailable players: {}", e.getMessage(), e);
            // Return empty lists if scraping fails
        }

        return unavailablePlayers;
    }

    /**
     * Scrapes recent matches for both teams.
     *
     * @param driver WebDriver instance
     * @param homeTeam The home team
     * @param awayTeam The away team
     * @return Map of recent matches for both teams
     */
    private Map<String, List<String>> scrapeRecentMatches(WebDriver driver, String homeTeam, String awayTeam) {
        Map<String, List<String>> recentMatches = new HashMap<>();
        recentMatches.put("home", new ArrayList<>());
        recentMatches.put("away", new ArrayList<>());
        recentMatches.put("between", new ArrayList<>());

        try {
            // This is a placeholder implementation
            // You would need to implement the actual scraping logic
            String url = "https://www.example.com/recent-matches/" + homeTeam.toLowerCase() + "-vs-" + awayTeam.toLowerCase();
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> homeRecentElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".home-team .recent-matches li")));

            List<WebElement> awayRecentElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".away-team .recent-matches li")));

            List<WebElement> betweenRecentElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".head-to-head .recent-matches li")));

            recentMatches.put("home", homeRecentElements.stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList()));

            recentMatches.put("away", awayRecentElements.stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList()));

            recentMatches.put("between", betweenRecentElements.stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("Error scraping recent matches: {}", e.getMessage(), e);
            // Return empty lists if scraping fails
        }

        return recentMatches;
    }

    /**
     * Scrapes team lineups for both teams.
     *
     * @param driver WebDriver instance
     * @param homeTeam The home team
     * @param awayTeam The away team
     * @return Team lineups for both teams
     */
    private TeamLineups scrapeTeamLineups(WebDriver driver, String homeTeam, String awayTeam) {
        TeamLineups teamLineups = new TeamLineups();

        try {
            // This is a placeholder implementation
            // You would need to implement the actual scraping logic
            String url = "https://www.example.com/lineups/" + homeTeam.toLowerCase() + "-vs-" + awayTeam.toLowerCase();
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Get home team formation
            WebElement homeFormationElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".home-team .formation")));
            String homeFormation = homeFormationElement.getText();

            // Get away team formation
            WebElement awayFormationElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".away-team .formation")));
            String awayFormation = awayFormationElement.getText();

            // Get home team players
            List<WebElement> homePlayerElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".home-team .players .player")));

            // Get away team players
            List<WebElement> awayPlayerElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".away-team .players .player")));

            // Process home team players
            List<PlayerPosition> homePlayers = homePlayerElements.stream()
                    .map(this::extractPlayerPosition)
                    .collect(Collectors.toList());

            // Process away team players
            List<PlayerPosition> awayPlayers = awayPlayerElements.stream()
                    .map(this::extractPlayerPosition)
                    .collect(Collectors.toList());

            // Set team formations
            TeamFormation homeTeamFormation = new TeamFormation(homeFormation, homePlayers);
            TeamFormation awayTeamFormation = new TeamFormation(awayFormation, awayPlayers);

            teamLineups.setHome(homeTeamFormation);
            teamLineups.setAway(awayTeamFormation);
        } catch (Exception e) {
            logger.error("Error scraping team lineups: {}", e.getMessage(), e);
            // Return empty lineups if scraping fails
        }

        return teamLineups;
    }

    /**
     * Extracts player position from a WebElement.
     *
     * @param playerElement WebElement containing player information
     * @return PlayerPosition object
     */
    private PlayerPosition extractPlayerPosition(WebElement playerElement) {
        try {
            WebElement numberElement = playerElement.findElement(By.cssSelector(".number"));
            WebElement nameElement = playerElement.findElement(By.cssSelector(".name"));
            WebElement positionElement = playerElement.findElement(By.cssSelector(".position"));

            Integer number = Integer.parseInt(numberElement.getText());
            String name = nameElement.getText();
            String position = positionElement.getText();

            return new PlayerPosition(number, name, position);
        } catch (Exception e) {
            logger.error("Error extracting player position: {}", e.getMessage(), e);
            return new PlayerPosition(null, "Unknown", "Unknown");
        }
    }

    /**
     * Scrapes standings for both teams.
     *
     * @param driver WebDriver instance
     * @param homeTeam The home team
     * @param awayTeam The away team
     * @return Standings for both teams
     */
    private StandingsResult scrapeStandings(WebDriver driver, String homeTeam, String awayTeam) {
        StandingsResult standingsResult = new StandingsResult();

        try {
            // This is a placeholder implementation
            // You would need to implement the actual scraping logic
            String url = "https://www.example.com/standings";
            driver.get(url);

            // Scrape home team standings
            TeamStanding homeStanding = scrapeTeamStanding(driver, homeTeam);
            standingsResult.setHome(homeStanding);

            // Scrape away team standings
            TeamStanding awayStanding = scrapeTeamStanding(driver, awayTeam);
            standingsResult.setAway(awayStanding);
        } catch (Exception e) {
            logger.error("Error scraping standings: {}", e.getMessage(), e);
            // Return default standings if scraping fails
            standingsResult.setHome(createDefaultTeamStanding(homeTeam));
            standingsResult.setAway(createDefaultTeamStanding(awayTeam));
        }

        return standingsResult;
    }

    /**
     * Scrapes team standing for a specific team.
     *
     * @param driver WebDriver instance
     * @param team The team to scrape standings for
     * @return Team standing
     */
    private TeamStanding scrapeTeamStanding(WebDriver driver, String team) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Find the row for the team in the overall standings table
            WebElement overallRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//table[@id='overall-standings']//tr[contains(., '" + team + "')]")));

            // Find the row for the team in the home standings table
            WebElement homeRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//table[@id='home-standings']//tr[contains(., '" + team + "')]")));

            // Find the row for the team in the away standings table
            WebElement awayRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//table[@id='away-standings']//tr[contains(., '" + team + "')]")));

            // Extract overall standing data
            TeamStandingData overallData = extractTeamStandingData(overallRow);

            // Extract home standing data
            TeamStandingData homeData = extractTeamStandingData(homeRow);

            // Extract away standing data
            TeamStandingData awayData = extractTeamStandingData(awayRow);

            return new TeamStanding(overallData, homeData, awayData);
        } catch (Exception e) {
            logger.error("Error scraping team standing for {}: {}", team, e.getMessage(), e);
            return createDefaultTeamStanding(team);
        }
    }

    /**
     * Extracts team standing data from a WebElement.
     *
     * @param rowElement WebElement containing team standing data
     * @return TeamStandingData object
     */
    private TeamStandingData extractTeamStandingData(WebElement rowElement) {
        try {
            List<WebElement> cells = rowElement.findElements(By.tagName("td"));

            int position = Integer.parseInt(cells.get(0).getText());
            String team = cells.get(1).getText();
            int played = Integer.parseInt(cells.get(2).getText());
            int won = Integer.parseInt(cells.get(3).getText());
            int drawn = Integer.parseInt(cells.get(4).getText());
            int lost = Integer.parseInt(cells.get(5).getText());
            int goalsFor = Integer.parseInt(cells.get(6).getText());
            int goalsAgainst = Integer.parseInt(cells.get(7).getText());
            int goalDifference = Integer.parseInt(cells.get(8).getText());
            int points = Integer.parseInt(cells.get(9).getText());

            return new TeamStandingData(position, team, played, won, drawn, lost, goalsFor, goalsAgainst, goalDifference, points);
        } catch (Exception e) {
            logger.error("Error extracting team standing data: {}", e.getMessage(), e);
            return new TeamStandingData(0, "Unknown", 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    /**
     * Creates default team standing for a team.
     *
     * @param team The team to create default standing for
     * @return Default team standing
     */
    private TeamStanding createDefaultTeamStanding(String team) {
        TeamStandingData defaultData = new TeamStandingData(0, team, 0, 0, 0, 0, 0, 0, 0, 0);
        return new TeamStanding(defaultData, defaultData, defaultData);
    }

    /**
     * Saves match data to the database.
     *
     * @param matchData The match data to save
     */
    private void saveMatchDataToDatabase(MatchData matchData) {
        try {
            // Convert match data to JSON for storage
            // This is a simplified version, you would need to implement the actual conversion
            String matchDataJson = convertMatchDataToJson(matchData);

            // Check if the match data already exists
            List<Map<String, Object>> existingMatches = jdbcTemplate.queryForList(
                    "SELECT * FROM match_data WHERE id = ?",
                    matchData.getId()
            );

            if (existingMatches.isEmpty()) {
                // Insert new match data
                jdbcTemplate.update(
                        "INSERT INTO match_data (id, data) VALUES (?, ?)",
                        matchData.getId(), matchDataJson
                );
            } else {
                // Update existing match data
                jdbcTemplate.update(
                        "UPDATE match_data SET data = ? WHERE id = ?",
                        matchDataJson, matchData.getId()
                );
            }

            logger.info("Match data saved to database for {}", matchData.getId());
        } catch (Exception e) {
            logger.error("Error saving match data to database: {}", e.getMessage(), e);
            // Continue without saving to database
        }
    }

    /**
     * Converts match data to JSON for storage.
     *
     * @param matchData The match data to convert
     * @return JSON string representation of match data
     */
    private String convertMatchDataToJson(MatchData matchData) {
        // This is a placeholder implementation
        // You would need to implement the actual conversion using Jackson or Gson
        return "{}";
    }

    /**
     * Converts a database record to MatchData.
     *
     * @param record The database record
     * @return MatchData object
     */
    private MatchData convertDatabaseRecordToMatchData(Map<String, Object> record) {
        // This is a placeholder implementation
        // You would need to implement the actual conversion
        MatchData matchData = new MatchData();
        matchData.setId((String) record.get("id"));

        // Parse the JSON data from the database
        // This is a simplified version, you would need to implement the actual parsing

        return matchData;
    }
}