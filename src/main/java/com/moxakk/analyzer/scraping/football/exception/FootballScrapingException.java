package com.moxakk.analyzer.scraping.football.exception;

/**
 * Exception class for football scraping operations.
 * This class represents exceptions that occur during football scraping operations.
 */
public class FootballScrapingException extends RuntimeException {

    /**
     * Constructs a new FootballScrapingException with the specified detail message.
     *
     * @param message The detail message
     */
    public FootballScrapingException(String message) {
        super(message);
    }

    /**
     * Constructs a new FootballScrapingException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause
     */
    public FootballScrapingException(String message, Throwable cause) {
        super(message, cause);
    }
}