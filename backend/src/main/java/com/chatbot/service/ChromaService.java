package com.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class ChromaService {
    @Value("${chroma.url}")
    private String chromaUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String COLLECTION_NAME = "document_embeddings";
    private String collectionId = null;
    
    public void createCollection() {
        try {
            String url = chromaUrl + "/api/v1/collections";
            Map<String, Object> body = Map.of(
                "name", COLLECTION_NAME,
                "metadata", Map.of("description", "Document embeddings")
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getBody() != null) {
                collectionId = (String) response.getBody().get("id");
            }
            System.out.println("ChromaDB collection created successfully");
        } catch (Exception e) {
            System.out.println("ChromaDB collection might already exist, fetching ID...");
            getCollectionId();
        }
    }
    
    private void getCollectionId() {
        try {
            String url = chromaUrl + "/api/v1/collections/" + COLLECTION_NAME;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getBody() != null) {
                collectionId = (String) response.getBody().get("id");
            }
        } catch (Exception e) {
            System.err.println("Failed to get collection ID: " + e.getMessage());
        }
    }
    
    public void addEmbedding(String id, String text, List<Double> embedding, Map<String, Object> metadata) {
        try {
            if (collectionId == null) getCollectionId();
            String url = chromaUrl + "/api/v1/collections/" + collectionId + "/add";
            
            Map<String, Object> body = new HashMap<>();
            body.put("ids", List.of(id));
            body.put("documents", List.of(text));
            body.put("embeddings", List.of(embedding));
            body.put("metadatas", List.of(metadata));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            System.err.println("ChromaDB add embedding failed: " + e.getMessage());
        }
    }
    
    public List<Map<String, Object>> queryEmbeddings(List<Double> queryEmbedding, int limit, Long userId) {
        try {
            if (collectionId == null) getCollectionId();
            String url = chromaUrl + "/api/v1/collections/" + collectionId + "/query";
            
            Map<String, Object> body = new HashMap<>();
            body.put("query_embeddings", List.of(queryEmbedding));
            body.put("n_results", limit);
            body.put("where", Map.of("user_id", userId.toString()));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                List<List<String>> documents = (List<List<String>>) result.get("documents");
                List<Map<String, Object>> results = new ArrayList<>();
                
                if (documents != null && !documents.isEmpty()) {
                    for (String doc : documents.get(0)) {
                        results.add(Map.of("content", doc));
                    }
                }
                return results;
            }
        } catch (Exception e) {
            System.err.println("ChromaDB query failed: " + e.getMessage());
        }
        return new ArrayList<>();
    }
    
    public void deleteByDocumentId(Long documentId) {
        try {
            if (collectionId == null) getCollectionId();
            String url = chromaUrl + "/api/v1/collections/" + collectionId + "/delete";
            
            Map<String, Object> body = Map.of(
                "where", Map.of("document_id", documentId.toString())
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            System.err.println("ChromaDB delete failed: " + e.getMessage());
        }
    }
}
