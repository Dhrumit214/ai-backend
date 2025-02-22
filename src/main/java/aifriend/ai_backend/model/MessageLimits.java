package aifriend.ai_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name = "daily_count")
    private Integer dailyCount = 0;

    @Column(name = "weekly_count")
    private Integer weeklyCount = 0;

    @Column(name = "monthly_count")
    private Integer monthlyCount = 0;

    @Column(name = "max_daily_messages")
    private Integer maxDailyMessages = 30;

    @Column(name = "max_weekly_messages")
    private Integer maxWeeklyMessages = 210; // 30 * 7

    @Column(name = "max_monthly_messages")
    private Integer maxMonthlyMessages = 900; // 30 * 30

    @Column(name = "last_reset_date")
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

    public void resetDailyCounts() {
        this.dailyCount = 0;
        this.lastResetDate = LocalDate.now();
    }

    public void resetWeeklyCounts() {
        this.weeklyCount = 0;
    }

    public void resetMonthlyCounts() {
        this.monthlyCount = 0;
    }
}