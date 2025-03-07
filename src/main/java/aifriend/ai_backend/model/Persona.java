package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp; 
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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

    @Column(name = "personality_traits", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String personalityTraitsJson = "[]";
    
    @Column(name = "sample_messages", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String sampleMessagesJson = "[]";
    
    @Column(name = "proactive_templates", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String proactiveTemplatesJson = "[]";
    
    @Transient
    private List<String> personalityTraits;
    
    @Transient
    private List<String> sampleMessages;
    
    @Transient
    private List<String> proactiveTemplates;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_plan")
    private PlanType requiredPlan = PlanType.FREE;
    
    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private OffsetDateTime modifiedAt;

    // Helper methods for JSON fields
    public List<String> getPersonalityTraits() {
        if (personalityTraitsJson == null || personalityTraitsJson.equals("[]")) {
            return new ArrayList<>();
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(personalityTraitsJson, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    public void setPersonalityTraits(List<String> traits) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.personalityTraitsJson = objectMapper.writeValueAsString(traits);
        } catch (IOException e) {
            this.personalityTraitsJson = "[]";
        }
    }
    
    public List<String> getSampleMessages() {
        if (sampleMessagesJson == null || sampleMessagesJson.equals("[]")) {
            return Arrays.asList("Hey there! How's your day going?");
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(sampleMessagesJson, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            return Arrays.asList("Hey there! How's your day going?");
        }
    }
    
    public void setSampleMessages(List<String> messages) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.sampleMessagesJson = objectMapper.writeValueAsString(messages);
        } catch (IOException e) {
            this.sampleMessagesJson = "[]";
        }
    }
    
    public List<String> getProactiveTemplates() {
        if (proactiveTemplatesJson == null || proactiveTemplatesJson.equals("[]")) {
            return new ArrayList<>();
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(proactiveTemplatesJson, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    public void setProactiveTemplates(List<String> templates) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.proactiveTemplatesJson = objectMapper.writeValueAsString(templates);
        } catch (IOException e) {
            this.proactiveTemplatesJson = "[]";
        }
    }
    
    // Helper method to get the sample messages (for backward compatibility)
    public List<String> getSampleMessagesList() {
        return getSampleMessages();
    }
}
