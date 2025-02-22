package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "personas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "system_prompt", columnDefinition = "TEXT")
    private String systemPrompt;

    @Column(name = "sample_messages")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object sampleMessages;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    public enum PlanType {
        free, basic, premium, vip
    }

    // Constructor for creating a new persona
    public Persona(String name, String systemPrompt, String description, String sampleMessages) {
        this.name = name;
        this.systemPrompt = systemPrompt;
        this.description = description;
        this.sampleMessages = sampleMessages;
    }

    @SuppressWarnings("unchecked")
    public List<String> getSampleMessages() {
        if (this.sampleMessages instanceof String) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue((String) this.sampleMessages, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
        return this.sampleMessages != null ? (List<String>) this.sampleMessages : new ArrayList<>();
    }
}
