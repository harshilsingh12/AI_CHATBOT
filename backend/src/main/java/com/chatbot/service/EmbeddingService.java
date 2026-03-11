package com.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {
    @Value("${huggingface.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public List<Double> generateEmbedding(String text) {
        // Try local embedding service first
        try {
            String url = "http://localhost:5000/embed";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> body = Map.of("text", text);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            if (response.getBody() != null) {
                List<Double> embedding = (List<Double>) response.getBody().get("embedding");
                return embedding;
            }
        } catch (Exception e) {
            System.err.println("Local embedding service not available: " + e.getMessage());
        }
        
        // Fallback to dummy embeddings if local service is down
        System.err.println("Using dummy embeddings - please start embedding service");
        List<Double> dummy = new ArrayList<>();
        for (int i = 0; i < 384; i++) {
            dummy.add(Math.random());
        }
        return dummy;
    }
    
    public List<String> chunkText(String text, int chunkSize) {
        String[] sentences = text.split("\\. ");
        java.util.List<String> chunks = new java.util.ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        
        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > chunkSize && currentChunk.length() > 0) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }
            currentChunk.append(sentence).append(". ");
        }
        
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        
        return chunks;
    }
}
