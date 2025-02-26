package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MessageId.class)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Id
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
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
    
    // The schema uses a composite primary key (id, created_at), and the foreign key references both
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Message parentMessage;
    
    @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Message> childMessages = new ArrayList<>();
    
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
