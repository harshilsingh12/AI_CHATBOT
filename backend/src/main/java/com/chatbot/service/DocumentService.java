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
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    
    public DocumentService(DocumentRepository documentRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }
    
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
        
        return "Document uploaded successfully";
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
    
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
    
    public String getDocumentContext(String username) {
        List<Document> docs = getUserDocuments(username);
        if (docs.isEmpty()) return null;
        
        StringBuilder context = new StringBuilder("Context from uploaded documents:\n\n");
        for (Document doc : docs) {
            context.append("File: ").append(doc.getFilename()).append("\n");
            context.append(doc.getContent()).append("\n\n");
        }
        return context.toString();
    }
}
