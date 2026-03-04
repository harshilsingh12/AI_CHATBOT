package com.chatbot.repository;

import com.chatbot.entity.Document;
import com.chatbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserOrderByUploadedAtDesc(User user);
}
