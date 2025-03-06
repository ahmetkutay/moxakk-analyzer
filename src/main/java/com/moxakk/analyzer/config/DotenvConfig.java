package com.moxakk.analyzer.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for loading environment variables from .env file.
 */
@Configuration
public class DotenvConfig {

    private static final Logger logger = LoggerFactory.getLogger(DotenvConfig.class);

    /**
     * Creates and configures a Dotenv bean.
     *
     * @return A configured Dotenv instance
     */
    @Bean
    public Dotenv dotenv() {
        try {
            logger.info("Loading environment variables from .env file");
            return Dotenv.configure().load();
        } catch (Exception e) {
            logger.warn("Failed to load .env file: {}", e.getMessage());
            logger.info("Using system environment variables instead");
            return Dotenv.configure().ignoreIfMissing().load();
        }
    }
}