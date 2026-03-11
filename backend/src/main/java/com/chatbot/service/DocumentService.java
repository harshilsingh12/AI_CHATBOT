package com.chatbot.service;

import com.chatbot.entity.Document;
import com.chatbot.entity.User;
import com.chatbot.repository.DocumentRepository;
import com.chatbot.repository.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final EmbeddingService embeddingService;
    private final ChromaService chromaService;
    
    public DocumentService(DocumentRepository documentRepository, UserRepository userRepository,
                          EmbeddingService embeddingService, ChromaService chromaService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.embeddingService = embeddingService;
        this.chromaService = chromaService;
        
        // Initialize ChromaDB collection
        chromaService.createCollection();
    }
    
    @Transactional
    public String uploadDocument(String username, MultipartFile file) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String content = extractText(file);
        
        Document document = new Document();
        document.setUser(user);
        document.setFilename(file.getOriginalFilename());
        document.setContent(content);
        document.setUploadedAt(LocalDateTime.now());
        documentRepository.save(document);
        
        // Create chunks and store in ChromaDB
        List<String> chunks = embeddingService.chunkText(content, 500);
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            List<Double> embedding = embeddingService.generateEmbedding(chunk);
            
            if (embedding != null) {
                String id = document.getId() + "_" + i;
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("document_id", document.getId().toString());
                metadata.put("user_id", user.getId().toString());
                metadata.put("filename", file.getOriginalFilename());
                metadata.put("chunk_index", String.valueOf(i));
                
                chromaService.addEmbedding(id, chunk, embedding, metadata);
            }
        }
        
        return "Document uploaded and processed with " + chunks.size() + " chunks";
    }
    
    private String extractText(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new Exception("Invalid file");
        
        if (filename.endsWith(".txt")) {
            return new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
        } else if (filename.endsWith(".pdf")) {
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        } else if (filename.endsWith(".docx")) {
            XWPFDocument document = new XWPFDocument(file.getInputStream());
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }
            document.close();
            return text.toString();
        } else {
            throw new Exception("Unsupported file type. Only .txt, .pdf, .docx supported");
        }
    }
    
    public List<Document> getUserDocuments(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return documentRepository.findByUserOrderByUploadedAtDesc(user);
    }
    
    @Transactional
    public void deleteDocument(Long id) {
        chromaService.deleteByDocumentId(id);
        documentRepository.deleteById(id);
    }
    
    public String getRelevantContext(String username, String query) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Double> queryEmbedding = embeddingService.generateEmbedding(query);
        if (queryEmbedding == null) return null;
        
        List<Map<String, Object>> results = chromaService.queryEmbeddings(queryEmbedding, 3, user.getId());
        
        if (results.isEmpty()) return null;
        
        StringBuilder context = new StringBuilder("Relevant context from documents:\n\n");
        for (Map<String, Object> result : results) {
            context.append(result.get("content")).append("\n\n");
        }
        
        return context.toString();
    }
}
