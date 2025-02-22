package aifriend.ai_backend.repository;

import aifriend.ai_backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findFirstByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Message> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    List<Message> findByUserIdAndConversationId(Long userId, String conversationId);
    
    @Query("SELECT m FROM Message m WHERE m.userId = ?1 AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    List<Message> findActiveMessagesByUserId(Long userId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.userId = ?1 AND m.createdAt >= ?2")
    Long countMessagesSince(Long userId, LocalDateTime since);
}