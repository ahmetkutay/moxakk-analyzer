package com.moxakk.analyzer.scraping.football.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception handler for football scraping operations.
 * This class handles exceptions that occur during football scraping operations.
 */
@ControllerAdvice
public class FootballScrapingExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(FootballScrapingExceptionHandler.class);

    /**
     * Handles FootballScrapingException.
     *
     * @param ex The exception
     * @return A response entity containing the error message
     */
    @ExceptionHandler(FootballScrapingException.class)
    public ResponseEntity<Map<String, Object>> handleFootballScrapingException(FootballScrapingException ex) {
        logger.error("Football scraping exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(ex.getMessage()));
    }

    /**
     * Handles ScrapingError.
     *
     * @param ex The exception
     * @return A response entity containing the error message
     */
    @ExceptionHandler(ScrapingError.class)
    public ResponseEntity<Map<String, Object>> handleScrapingError(ScrapingError ex) {
        logger.error("Scraping error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(ex.getMessage()));
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
}