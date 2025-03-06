package com.moxakk.analyzer.scraping.football.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating WebDriver instances.
 * This class provides methods to create and configure WebDriver instances for web scraping.
 */
@Component
public class WebDriverFactory {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    /**
     * Creates a new WebDriver instance with default configuration.
     *
     * @return A configured WebDriver instance
     */
    public WebDriver createWebDriver() {
        try {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-extensions");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--user-agent=" + USER_AGENT);

            return new ChromeDriver(options);
        } catch (Exception e) {
            logger.error("Error creating WebDriver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create WebDriver instance", e);
        }
    }
}