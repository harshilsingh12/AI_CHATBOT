package com.chatbot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "document_chunks")
@Data
public class DocumentChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String embedding;
    
    private Integer chunkIndex;
}
