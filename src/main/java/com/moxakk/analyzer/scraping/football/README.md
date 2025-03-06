# Football Scraping Module

This module provides functionality for scraping football match data from websites and generating commentary using AI services.

## Overview

The Football Scraping Module is designed to scrape football match data from websites and generate commentary using various AI services. It provides a RESTful API for analyzing football matches and generating commentary.

## Components

### Models

- `MatchData`: Represents football match data
- `WeatherData`: Represents weather data for a football match
- `PlayerPosition`: Represents a player's position in a team
- `TeamFormation`: Represents a team's formation
- `TeamLineups`: Represents team lineups for a match
- `TeamStandingData`: Represents team standing data
- `TeamStanding`: Represents team standings
- `StandingsResult`: Represents standings results

### Services

- `FootballScrapingService`: High-level API for football scraping operations
- `FootballMatchAnalyzer`: Analyzes football matches by scraping data from websites
- `FootballCommentaryService`: Generates commentary for football matches using AI services
- `WeatherService`: Retrieves weather data for a venue
- `AIService`: Interacts with various AI services

### Controllers

- `FootballMatchController`: Provides endpoints for analyzing football matches

### Exceptions

- `FootballScrapingException`: Exception for football scraping operations
- `ScrapingError`: Exception for scraping errors

### Utilities

- `WebDriverFactory`: Factory for creating WebDriver instances

## API Endpoints

### Analyze Football Match

```
POST /api/get-match
```

Request Body:
```json
{
  "homeTeam": "Manchester United",
  "awayTeam": "Liverpool"
}
```

Response:
```json
{
  "matchData": {
    "id": "Manchester United-Liverpool",
    "matchInput": "Manchester United-Liverpool",
    "homeTeam": "Manchester United",
    "awayTeam": "Liverpool",
    "venue": "Old Trafford",
    "weather": {
      "temperature": 15.5,
      "condition": "Cloudy",
      "humidity": 75,
      "windSpeed": 5.2
    },
    "unavailablePlayers": {
      "home": ["Player 1", "Player 2"],
      "away": ["Player 3", "Player 4"]
    },
    "recentMatches": {
      "home": ["Match 1", "Match 2"],
      "away": ["Match 3", "Match 4"],
      "between": ["Match 5", "Match 6"]
    },
    "teamLineups": {
      "home": {
        "formation": "4-3-3",
        "players": [
          {
            "number": 1,
            "name": "Player 1",
            "position": "GK"
          },
          // ... more players
        ]
      },
      "away": {
        "formation": "4-4-2",
        "players": [
          {
            "number": 1,
            "name": "Player 1",
            "position": "GK"
          },
          // ... more players
        ]
      }
    },
    "standings": {
      "home": {
        "overall": {
          "position": 3,
          "team": "Manchester United",
          "played": 10,
          "won": 7,
          "drawn": 2,
          "lost": 1,
          "goalsFor": 20,
          "goalsAgainst": 10,
          "goalDifference": 10,
          "points": 23
        },
        "homeForm": {
          // ... similar to overall
        },
        "awayForm": {
          // ... similar to overall
        }
      },
      "away": {
        // ... similar to home
      }
    }
  },
  "commentary": [
    "Commentary from Gemini",
    "Commentary from OpenAI",
    "Commentary from Cohere",
    "Commentary from Anthropic",
    "Commentary from Mistral"
  ]
}
```

## Configuration

The module requires the following environment variables:

- `GOOGLE_API_KEY`: Google API key for Gemini
- `OPENAI_API_KEY`: OpenAI API key
- `COHERE_API_KEY`: Cohere API key
- `ANTHROPIC_API_KEY`: Anthropic API key
- `MISTRAL_API_KEY`: Mistral API key
- `OPENWEATHER_API_KEY`: OpenWeather API key

## Dependencies

- Selenium: For web scraping
- WebDriverManager: For managing WebDriver instances
- Spring Boot: For RESTful API and dependency injection
- Jackson: For JSON processing
- RestTemplate: For making HTTP requests