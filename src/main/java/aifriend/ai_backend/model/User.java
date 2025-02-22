package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import aifriend.ai_backend.model.PlanType;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number_hash", nullable = false)
    private String phoneNumberHash;

    @Column(name = "phone_number_encrypted")
    private byte[] phoneNumberEncrypted;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type")
    private PlanType planType = PlanType.free;

    @Column(name = "settings", columnDefinition = "jsonb")
    private String settings = "{}";

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

    // For Twilio integration - decrypted phone number
    @Transient
    private String phoneNumber;

    public String getPhoneNumber() {
        // TODO: Implement decryption logic
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        // TODO: Implement encryption logic for phoneNumberEncrypted
        // TODO: Implement hashing logic for phoneNumberHash
    }
}