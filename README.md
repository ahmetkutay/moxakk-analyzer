# Moxakk Analyzer

A Spring Boot application for analyzing football matches and generating commentary using AI services.

## Overview

Moxakk Analyzer is a Spring Boot application that provides functionality for analyzing football matches and generating commentary using AI services. It scrapes football match data from websites and uses various AI services to generate commentary.

## Features

- Football match analysis
- AI-powered commentary generation
- Weather data integration
- RESTful API

## Modules

### Football Scraping Module

The Football Scraping Module provides functionality for scraping football match data from websites and generating commentary using AI services. It includes:

- Models for representing football match data
- Services for analyzing football matches and generating commentary
- Controllers for providing RESTful API endpoints
- Utilities for web scraping

For more information, see the [Football Scraping Module README](src/main/java/com/moxakk/analyzer/scraping/football/README.md).

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
    // Football match data
  },
  "commentary": [
    // Commentary from various AI services
  ]
}
```

## Configuration

The application requires the following environment variables:

- `GOOGLE_API_KEY`: Google API key for Gemini
- `OPENAI_API_KEY`: OpenAI API key
- `COHERE_API_KEY`: Cohere API key
- `ANTHROPIC_API_KEY`: Anthropic API key
- `MISTRAL_API_KEY`: Mistral API key
- `OPENWEATHER_API_KEY`: OpenWeather API key
- `POSTGRES_USER`: PostgreSQL username
- `POSTGRES_PASSWORD`: PostgreSQL password
- `POSTGRES_DB`: PostgreSQL database name
- `POSTGRES_HOST`: PostgreSQL host
- `POSTGRES_PORT`: PostgreSQL port
- `JWT_SECRET`: JWT secret key
- `JWT_EXPIRATION`: JWT expiration time in milliseconds

## Dependencies

- Spring Boot: For RESTful API and dependency injection
- Selenium: For web scraping
- WebDriverManager: For managing WebDriver instances
- Jackson: For JSON processing
- RestTemplate: For making HTTP requests
- PostgreSQL: For data persistence
- JWT: For authentication
- Thymeleaf: For server-side rendering

## Getting Started

### Prerequisites

- Java 17 or later
- PostgreSQL
- Chrome browser (for Selenium)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/moxakk-analyzer.git
cd moxakk-analyzer
```

2. Create a `.env` file with the required environment variables.

3. Build the application:
```bash
./gradlew build
```

4. Run the application:
```bash
./gradlew bootRun
```

The application will be available at http://localhost:8081.

## License

This project is licensed under the MIT License - see the LICENSE file for details.