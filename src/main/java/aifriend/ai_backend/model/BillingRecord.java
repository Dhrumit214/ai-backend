package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "message_count")
    private Integer messageCount = 0;
    
    @Column(name = "billing_period", nullable = false)
    private String billingPeriod; // Format: YYYY-MM
    
    @Column(name = "amount_due", precision = 10, scale = 2)
    private BigDecimal amountDue = BigDecimal.ZERO;
    
    @Column(name = "rate_per_message", precision = 10, scale = 3)
    private BigDecimal ratePerMessage = new BigDecimal("0.018");
    
    @Column(name = "is_paid")
    private Boolean isPaid = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;
    
    public void calculateAmountDue() {
        if (this.messageCount != null && this.ratePerMessage != null) {
            this.amountDue = this.ratePerMessage.multiply(new BigDecimal(this.messageCount));
        }
    }
} 