package aifriend.ai_backend.service;

import aifriend.ai_backend.repository.MessageLimitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MessageLimitsScheduler {

    @Autowired
    private MessageLimitsRepository messageLimitsRepository;

    // Reset daily counts at midnight every day
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void resetDailyCounts() {
        messageLimitsRepository.findAll().forEach(limit -> {
            limit.resetDailyCounts();
            messageLimitsRepository.save(limit);
        });
        System.out.println("Daily message counts reset at " + LocalDateTime.now());
    }

    // Reset weekly counts at midnight on Sunday
    @Scheduled(cron = "0 0 0 ? * SUN")
    @Transactional
    public void resetWeeklyCounts() {
        messageLimitsRepository.findAll().forEach(limit -> {
            limit.resetWeeklyCounts();
            messageLimitsRepository.save(limit);
        });
        System.out.println("Weekly message counts reset at " + LocalDateTime.now());
    }

    // Reset monthly counts at midnight on the first day of each month
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void resetMonthlyCounts() {
        messageLimitsRepository.findAll().forEach(limit -> {
            limit.resetMonthlyCounts();
            messageLimitsRepository.save(limit);
        });
        System.out.println("Monthly message counts reset at " + LocalDateTime.now());
    }
} 