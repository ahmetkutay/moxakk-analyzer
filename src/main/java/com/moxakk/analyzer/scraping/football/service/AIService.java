package com.moxakk.analyzer.scraping.football.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service for interacting with AI providers.
 */
@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    @Value("${google.api.key}")
    private String googleApiKey;

    @Value("${openai.api.key}")
    private String openAIApiKey;

    @Value("${cohere.api.key}")
    private String cohereApiKey;

    @Value("${anthropic.api.key}")
    private String anthropicApiKey;

    @Value("${mistral.api.key}")
    private String mistralApiKey;

    private final RestTemplate restTemplate;

    public AIService(@Qualifier("footballRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gets a response from Google's Gemini AI.
     *
     * @param prompt The prompt to send to the AI
     * @return The AI's response
     */
    public String getGeminiResponse(String prompt) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + googleApiKey;

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();

            part.put("text", prompt);
            content.put("parts", new Object[]{part});
            requestBody.put("contents", new Object[]{content});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // Extract the response text from the Gemini API response
            // This is a simplified implementation and may need to be adjusted based on the actual API response structure
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("candidates")) {
                Object[] candidates = (Object[]) responseBody.get("candidates");
                if (candidates.length > 0) {
                    Map<String, Object> candidate = (Map<String, Object>) candidates[0];
                    Map<String, Object> content1 = (Map<String, Object>) candidate.get("content");
                    Object[] parts = (Object[]) content1.get("parts");
                    Map<String, Object> part1 = (Map<String, Object>) parts[0];
                    return (String) part1.get("text");
                }
            }

            return "Failed to get response from Gemini";
        } catch (Exception e) {
            logger.error("Error getting Gemini response: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Gets a response from OpenAI.
     *
     * @param prompt The prompt to send to the AI
     * @return The AI's response
     */
    public String getOpenAIResponse(String prompt) {
        try {
            String url = "https://api.openai.com/v1/chat/completions";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4");

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            requestBody.put("messages", new Object[]{message});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAIApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // Extract the response text from the OpenAI API response
            // This is a simplified implementation and may need to be adjusted based on the actual API response structure
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                Object[] choices = (Object[]) responseBody.get("choices");
                if (choices.length > 0) {
                    Map<String, Object> choice = (Map<String, Object>) choices[0];
                    Map<String, Object> message1 = (Map<String, Object>) choice.get("message");
                    return (String) message1.get("content");
                }
            }

            return "Failed to get response from OpenAI";
        } catch (Exception e) {
            logger.error("Error getting OpenAI response: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Gets a response from Cohere.
     *
     * @param prompt The prompt to send to the AI
     * @return The AI's response
     */
    public String getCohereResponse(String prompt) {
        try {
            String url = "https://api.cohere.ai/v1/generate";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "command");
            requestBody.put("prompt", prompt);
            requestBody.put("max_tokens", 500);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + cohereApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // Extract the response text from the Cohere API response
            // This is a simplified implementation and may need to be adjusted based on the actual API response structure
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("generations")) {
                Object[] generations = (Object[]) responseBody.get("generations");
                if (generations.length > 0) {
                    Map<String, Object> generation = (Map<String, Object>) generations[0];
                    return (String) generation.get("text");
                }
            }

            return "Failed to get response from Cohere";
        } catch (Exception e) {
            logger.error("Error getting Cohere response: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Gets a response from Anthropic.
     *
     * @param prompt The prompt to send to the AI
     * @return The AI's response
     */
    public String getAnthropicResponse(String prompt) {
        try {
            String url = "https://api.anthropic.com/v1/messages";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "claude-3-opus-20240229");
            requestBody.put("max_tokens", 500);
            requestBody.put("system", "You are a helpful assistant that provides football match analysis and predictions.");

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            requestBody.put("messages", new Object[]{message});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // Extract the response text from the Anthropic API response
            // This is a simplified implementation and may need to be adjusted based on the actual API response structure
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("content")) {
                Object[] content = (Object[]) responseBody.get("content");
                if (content.length > 0) {
                    Map<String, Object> contentItem = (Map<String, Object>) content[0];
                    return (String) contentItem.get("text");
                }
            }

            return "Failed to get response from Anthropic";
        } catch (Exception e) {
            logger.error("Error getting Anthropic response: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Gets a response from Mistral.
     *
     * @param prompt The prompt to send to the AI
     * @return The AI's response
     */
    public String getMistralResponse(String prompt) {
        try {
            String url = "https://api.mistral.ai/v1/chat/completions";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "mistral-large-latest");

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            requestBody.put("messages", new Object[]{message});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(mistralApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // Extract the response text from the Mistral API response
            // This is a simplified implementation and may need to be adjusted based on the actual API response structure
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                Object[] choices = (Object[]) responseBody.get("choices");
                if (choices.length > 0) {
                    Map<String, Object> choice = (Map<String, Object>) choices[0];
                    Map<String, Object> message1 = (Map<String, Object>) choice.get("message");
                    return (String) message1.get("content");
                }
            }

            return "Failed to get response from Mistral";
        } catch (Exception e) {
            logger.error("Error getting Mistral response: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }
}