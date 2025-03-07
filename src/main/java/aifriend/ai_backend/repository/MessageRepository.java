package aifriend.ai_backend.repository;

import aifriend.ai_backend.model.Message;
import aifriend.ai_backend.model.MessageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, MessageId> {
    Message findFirstByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Message> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    List<Message> findByUserIdAndConversationId(Long userId, UUID conversationId);
    
    @Query("SELECT m FROM Message m WHERE m.userId = ?1 AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    List<Message> findActiveMessagesByUserId(Long userId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.userId = ?1 AND m.createdAt >= ?2")
    Long countMessagesSince(Long userId, LocalDateTime since);

    List<Message> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Message> findByPersonaId(Long personaId);
    
    long countByCreatedAtAfter(LocalDateTime dateTime);
    
    // New method to find a message by its id and return results ordered by createdAt
    @Query("SELECT m FROM Message m WHERE m.id = ?1 ORDER BY m.createdAt DESC")
    Optional<Message> findByIdOrderByCreatedAtDesc(Long id);
}
