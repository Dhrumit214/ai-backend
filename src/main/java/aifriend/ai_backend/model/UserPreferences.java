package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "preferred_persona_id")
    private Long preferredPersonaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_frequency", nullable = false)
    private MessageFrequency messageFrequency = MessageFrequency.DAILY;

    @Column(name = "do_not_disturb")
    private Boolean doNotDisturb = false;
    
    @Column(name = "max_daily_messages")
    private Integer maxDailyMessages = 30;
    
    @Column(name = "preferred_time_window", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String preferredTimeWindow = "{\"start\": \"09:00\", \"end\": \"21:00\"}";
    
    @Column(name = "notification_settings", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String notificationSettings = "{\"sms\": true, \"email\": false}";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private OffsetDateTime modifiedAt;
}