package com.chatbot.repository;

import com.chatbot.entity.Document;
import com.chatbot.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {
    List<DocumentChunk> findByDocument(Document document);
    
    @Query(value = "SELECT * FROM document_chunks dc " +
           "WHERE dc.document_id IN (SELECT d.id FROM documents d WHERE d.user_id = :userId) " +
           "ORDER BY dc.embedding <-> CAST(:queryEmbedding AS vector) " +
           "LIMIT :limit", nativeQuery = true)
    List<DocumentChunk> findSimilarChunks(@Param("userId") Long userId, 
                                          @Param("queryEmbedding") String queryEmbedding, 
                                          @Param("limit") int limit);
    
    void deleteByDocument(Document document);
}
