package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "persona_id")
    private Long personaId;
    
    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId = UUID.randomUUID();
    
    @Column(name = "user_message")
    private String userMessage;
    
    @Column(name = "ai_response")
    private String aiResponse;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageContentType messageType = MessageContentType.TEXT;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment", nullable = false)
    private SentimentType sentiment = SentimentType.NEUTRAL;
    
    @Column(name = "tags", columnDefinition = "text[]")
    private String[] tags;
    
    @Column(name = "context", columnDefinition = "jsonb")
    private String context;
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;
    
    @Column(name = "user_message_url")
    private String userMessageUrl;
    
    @Column(name = "ai_response_url")
    private String aiResponseUrl;
    
    @Column(name = "is_billable", nullable = false)
    private Boolean isBillable = false;
    
    @Column(name = "billed", nullable = false)
    private Boolean billed = false;
    
    @ManyToOne
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 1;
    
    // Constructor for basic message creation
    public Message(Long userId, String userMessage, String aiResponse, String personaName, String sentiment) {
        this.userId = userId;
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.sentiment = SentimentType.fromValue(sentiment);
    }
}
