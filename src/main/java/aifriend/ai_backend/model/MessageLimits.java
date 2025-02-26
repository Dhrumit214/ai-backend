package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_limits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageLimits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "daily_count", nullable = false)
    private Integer dailyCount = 0;
    
    @Column(name = "weekly_count", nullable = false)
    private Integer weeklyCount = 0;
    
    @Column(name = "monthly_count", nullable = false)
    private Integer monthlyCount = 0;
    
    @Column(name = "billable_count", nullable = false)
    private Integer billableCount = 0;
    
    @Column(name = "unbilled_amount", nullable = false, precision = 10, scale = 4)
    private BigDecimal unbilledAmount = BigDecimal.ZERO;
    
    @Column(name = "last_billing_date")
    private LocalDateTime lastBillingDate;
    
    @Column(name = "free_limit", nullable = false)
    private Integer freeLimit = 30;
    
    @Column(name = "max_daily_messages", nullable = false)
    private Integer maxDailyMessages = 30;
    
    @Column(name = "max_weekly_messages", nullable = false)
    private Integer maxWeeklyMessages = 210;
    
    @Column(name = "max_monthly_messages", nullable = false)
    private Integer maxMonthlyMessages = 900;
    
    @Column(name = "last_reset_date", nullable = false)
    private LocalDate lastResetDate = LocalDate.now();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;
    
    public void incrementCounts() {
        this.dailyCount++;
        this.weeklyCount++;
        this.monthlyCount++;
    }

    /**
     * Reset the daily message count to zero
     */
    public void resetDailyCounts() {
        this.dailyCount = 0;
    }

    /**
     * Reset the weekly message count to zero
     */
    public void resetWeeklyCounts() {
        this.weeklyCount = 0;
    }

    /**
     * Reset the monthly message count to zero
     */
    public void resetMonthlyCounts() {
        this.monthlyCount = 0;
    }
    
    /**
     * Increment billable message count and add to unbilled amount
     */
    public void incrementBillableCount() {
        this.billableCount++;
        this.unbilledAmount = this.unbilledAmount.add(new BigDecimal("0.018"));
    }
    
    /**
     * Reset billable counts after billing
     */
    public void resetBillingCounts() {
        this.billableCount = 0;
        this.unbilledAmount = BigDecimal.ZERO;
        this.lastBillingDate = LocalDateTime.now();
    }
    
    /**
     * Check if a message should be billable based on daily count
     */
    public boolean isMessageBillable() {
        return this.dailyCount > this.freeLimit;
    }
}
