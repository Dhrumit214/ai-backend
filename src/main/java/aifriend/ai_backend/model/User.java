package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import aifriend.ai_backend.util.SecurityUtils;

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

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Store encrypted phone number in the database
    @Column(name = "phone_number")
    private String phoneNumberEncrypted;
    
    // For phone number lookups (hashed)
    @Column(name = "phone_number_hash")
    private String phoneNumberHash;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType = PlanType.FREE;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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
                this.phoneNumber = securityUtils.decrypt(phoneNumberEncrypted);
                return this.phoneNumber;
            } else {
                // Fallback for scenarios where SecurityUtils isn't injected (like in DTOs)
                return phoneNumberEncrypted;
            }
        } catch (Exception e) {
            // If decryption fails, return encrypted value for development
            return phoneNumberEncrypted;
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
                this.phoneNumberEncrypted = securityUtils.encrypt(phoneNumber);
                this.phoneNumberHash = securityUtils.secureHash(phoneNumber);
            } else {
                // Fallback for when SecurityUtils is not injected
                this.phoneNumberEncrypted = phoneNumber;
                this.phoneNumberHash = String.valueOf(phoneNumber.hashCode());
            }
        } catch (Exception e) {
            // Fallback to simple storage in development
            this.phoneNumberEncrypted = phoneNumber;
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
        
        this.phoneNumberEncrypted = utils.encrypt(phoneNumber);
        this.phoneNumberHash = utils.secureHash(phoneNumber);
    }
}
