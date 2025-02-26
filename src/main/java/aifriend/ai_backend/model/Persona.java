package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    @Column(name = "system_prompt", columnDefinition = "text")
    private String systemPrompt;

    @Column(name = "description")
    private String description;

    @Column(name = "sample_messages", columnDefinition = "text")
    private String sampleMessagesJson;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_plan")
    private PlanType requiredPlan = PlanType.free;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    public enum PlanType {
        free,
        premium,
        enterprise
    }

    // Constructor for setting up default personas
    public Persona(String name, String systemPrompt, String description, String sampleMessagesJson) {
        this.name = name;
        this.systemPrompt = systemPrompt;
        this.description = description;
        this.sampleMessagesJson = sampleMessagesJson;
        this.isActive = true;
        this.requiredPlan = PlanType.free;
    }

    // Helper method to get the sample messages as a List
    public List<String> getSampleMessages() {
        if (sampleMessagesJson == null || sampleMessagesJson.isEmpty()) {
            return Arrays.asList("Hey there! How's your day going?");
        }
        
        // Simple parsing of the JSON array string (basic implementation)
        String cleaned = sampleMessagesJson.replaceAll("\\[|\\]|\\\"", "");
        return Arrays.asList(cleaned.split(","));
    }
}
