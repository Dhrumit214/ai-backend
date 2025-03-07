package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    
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
    
    @Column(name = "is_proactive", nullable = false)
    private Boolean isProactive = false;
    
    @Column(name = "topics")
    @JdbcTypeCode(SqlTypes.JSON)
    private String topicsJson = "[]";
    
    @Column(name = "context", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String context = "{}";
    
    @Column(name = "metadata", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata = "{}";
    
    @Column(name = "tags", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] tags;
    
    @Column(name = "user_message_url")
    private String userMessageUrl;
    
    @Column(name = "ai_response_url")
    private String aiResponseUrl;
    
    @Column(name = "is_billable", nullable = false)
    private Boolean isBillable = false;
    
    @Column(name = "billed", nullable = false)
    private Boolean billed = false;
    
    // Referenced by foreignKey with id only
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private Message parentMessage;
    
    @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    private List<Message> childMessages = new ArrayList<>();
    
    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private OffsetDateTime modifiedAt;
    
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
    
    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 1;
    
    // Constructor for basic message creation
    public Message(Long userId, String userMessage, String aiResponse, String personaName, String sentiment) {
        this.userId = userId;
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.sentiment = SentimentType.fromValue(sentiment);
        this.tags = new String[0]; // Initialize with empty array
    }
}
