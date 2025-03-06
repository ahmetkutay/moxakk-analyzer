package com.moxakk.analyzer.scraping.football.exception;

/**
 * Exception thrown when an error occurs during web scraping.
 */
public class ScrapingError extends RuntimeException {

    public ScrapingError(String message) {
        super(message);
    }

    public ScrapingError(String message, Throwable cause) {
        super(message, cause);
    }
}