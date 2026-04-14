package com.flighttracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class GeminiService { 

    @Value("${azure.openai.api.key}")
    private String azureApiKey;

    @Value("${azure.openai.endpoint}")
    private String azureEndpoint;

    @Value("${azure.openai.deployment.name}")
    private String deploymentName;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String generateFlightStory(String callsign, String dep, String arr, String aircraft, int altitude) {
        log.info("Asking Azure OpenAI to write a story for flight {}...", callsign);

        String systemPrompt = "You are a professional aviation expert writing a short, exciting summary for a flight tracking app. Make it sound cinematic but factual. Do not use asterisks or bold formatting.";
        String userPrompt = String.format("Write a 3-sentence paragraph about flight %s. It is a %s aircraft flying from %s to %s, currently cruising at %d feet.", callsign, aircraft, dep, arr, altitude);

        // THE FIX: Changed "max_tokens" to "max_completion_tokens"
        String requestBody = "{\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"" + systemPrompt + "\"},\n" +
                "    {\"role\": \"user\", \"content\": \"" + userPrompt + "\"}\n" +
                "  ],\n" +
                "  \"max_completion_tokens\": 150,\n" + 
                "  \"temperature\": 0.7\n" +
                "}";

        try {
            String baseUrl = azureEndpoint.endsWith("/") ? azureEndpoint : azureEndpoint + "/";
            
            // Construct the exact Azure OpenAI path
            String url = baseUrl + "openai/deployments/" + deploymentName + "/chat/completions?api-version=2024-02-15-preview";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", azureApiKey); 

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // Send the request to Azure
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // Parse Azure's response format
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            return rootNode.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            log.error("Azure AI Error: {}", e.getMessage());
            return "Flight " + callsign + " is currently en route. Live telemetry indicates it is maintaining an altitude of " + altitude + " feet. Radar contact is stable.";
        }
    }
}