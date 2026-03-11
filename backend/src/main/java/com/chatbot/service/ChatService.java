package com.chatbot.service;

import com.chatbot.entity.ChatMessage;
import com.chatbot.entity.User;
import com.chatbot.repository.ChatMessageRepository;
import com.chatbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatService {
    @Value("${groq.api.key}")
    private String apiKey;
    
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final DocumentService documentService;
    private final RestTemplate restTemplate = new RestTemplate();
    
    public ChatService(ChatMessageRepository chatMessageRepository, UserRepository userRepository, DocumentService documentService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.documentService = documentService;
    }
    
    public String chat(String username, String message) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String response = callHuggingFaceAPI(message);
        
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUser(user);
        chatMessage.setUserMessage(message);
        chatMessage.setBotResponse(response);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
        
        return response;
    }
    
    private String callHuggingFaceAPI(String message) {
        String url = "https://api.groq.com/openai/v1/chat/completions";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        
        // Get relevant document context using semantic search
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String documentContext = documentService.getRelevantContext(username, message);
        
        String userMessage = message;
        if (documentContext != null) {
            userMessage = documentContext + "\n\nUser Question: " + message + "\n\nPlease answer based on the provided context in 117 words or less.";
        } else {
            userMessage = message + "\n\nPlease provide a concise answer in 117 words or less.";
        }
        
        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.3-70b-versatile");
        body.put("messages", new Object[]{
            Map.of("role", "user", "content", userMessage)
        });
        body.put("max_tokens", 150);
        body.put("temperature", 0.7);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            if (response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Object choices = responseBody.get("choices");
                if (choices instanceof java.util.List && !((java.util.List<?>) choices).isEmpty()) {
                    Map<String, Object> choice = (Map<String, Object>) ((java.util.List<?>) choices).get(0);
                    Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");
                    String content = (String) messageObj.get("content");
                    return content != null && !content.isEmpty() ? content : "I'm processing your request.";
                }
            }
            return fallbackResponse(message);
        } catch (Exception e) {
            e.printStackTrace();
            return fallbackResponse(message);
        }
    }
    
    private String fallbackResponse(String message) {
        String lowerMsg = message.toLowerCase();
        if (lowerMsg.contains("hello") || lowerMsg.contains("hi")) {
            return "Hello! How can I help you today?";
        } else if (lowerMsg.contains("universe")) {
            return "The universe is all of space and time and their contents, including planets, stars, galaxies, and all other forms of matter and energy.";
        } else {
            return "I'm here to help! Could you rephrase that?";
        }
    }
}
