package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import aifriend.ai_backend.util.SecurityUtils;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "auth_id")
    private UUID authId;

    // Store encrypted phone number in the database
    @Column(name = "phone_number_encrypted", columnDefinition = "bytea")
    private byte[] phoneNumberEncrypted;
    
    // For phone number lookups (hashed)
    @Column(name = "phone_number_hash", nullable = false)
    private String phoneNumberHash;
    
    @Column(name = "settings", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String settings = "{}";
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "password_hash")
    private String passwordHash;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type")
    private PlanType planType = PlanType.FREE;
    
    @Column(name = "time_zone")
    private String timeZone = "UTC";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_status")
    private UserActivityStatus activityStatus = UserActivityStatus.OCCASIONAL;
    
    @Column(name = "last_active_at")
    private OffsetDateTime lastActiveAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private OffsetDateTime modifiedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    // For Twilio integration - decrypted phone number
    @Transient
    private String phoneNumber;
    
    @Transient
    @Autowired
    @Lazy
    private SecurityUtils securityUtils;

    public String getPhoneNumber() {
        if (phoneNumber != null) {
            return phoneNumber;
        }
        
        if (phoneNumberEncrypted == null) {
            return null;
        }
        
        try {
            // Try to decrypt using SecurityUtils if available
            if (securityUtils != null) {
                this.phoneNumber = securityUtils.decryptBytes(phoneNumberEncrypted);
                return this.phoneNumber;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        
        if (phoneNumber == null) {
            this.phoneNumberEncrypted = null;
            this.phoneNumberHash = null;
            return;
        }
        
        try {
            // Use SecurityUtils if available
            if (securityUtils != null) {
                this.phoneNumberEncrypted = securityUtils.encryptToBytes(phoneNumber);
                this.phoneNumberHash = securityUtils.secureHash(phoneNumber);
            } else {
                // Fallback for when SecurityUtils is not injected
                this.phoneNumberHash = String.valueOf(phoneNumber.hashCode());
            }
        } catch (Exception e) {
            // Fallback to simple storage in development
            this.phoneNumberHash = String.valueOf(phoneNumber.hashCode());
        }
    }
    
    // Helper method for services to set the phone number directly with security utils
    // (This is used when SecurityUtils can't be autowired directly in the entity)
    public void setPhoneNumberSecure(String phoneNumber, SecurityUtils utils) {
        this.phoneNumber = phoneNumber;
        
        if (phoneNumber == null) {
            this.phoneNumberEncrypted = null;
            this.phoneNumberHash = null;
            return;
        }
        
        this.phoneNumberEncrypted = utils.encryptToBytes(phoneNumber);
        this.phoneNumberHash = utils.secureHash(phoneNumber);
    }
}
