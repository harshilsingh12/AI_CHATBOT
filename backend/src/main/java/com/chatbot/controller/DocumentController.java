package com.chatbot.controller;

import com.chatbot.entity.Document;
import com.chatbot.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;
    
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file, 
                                                  Authentication authentication) {
        try {
            String message = documentService.uploadDocument(authentication.getName(), file);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Document>> getDocuments(Authentication authentication) {
        return ResponseEntity.ok(documentService.getUserDocuments(authentication.getName()));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok("Document deleted");
    }
}
