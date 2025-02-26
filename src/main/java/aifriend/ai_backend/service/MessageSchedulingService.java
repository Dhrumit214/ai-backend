package aifriend.ai_backend.service;

import aifriend.ai_backend.model.*;
import aifriend.ai_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Service
public class MessageSchedulingService {

    @Autowired
    private MessageService messageService;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private MessageLimitsRepository messageLimitsRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillingService billingService;

    private final Random random = new Random();

    // Run every 15 minutes
    @Scheduled(fixedRate = 900000)
    @Transactional
    public void scheduleMessages() {
        LocalDateTime now = LocalDateTime.now();
        
        // Get all active user preferences
        List<UserPreferences> activeUsers = userPreferencesRepository.findByDoNotDisturbFalse();
        
        for (UserPreferences prefs : activeUsers) {
            try {
                processUserPreferences(prefs, now);
            } catch (Exception e) {
                // Log error but continue processing other users
                System.err.println("Error processing user " + prefs.getUserId() + ": " + e.getMessage());
            }
        }
    }

    private void processUserPreferences(UserPreferences prefs, LocalDateTime now) {
        // Check if within user's preferred time window
        if (!isWithinPreferredTimeWindow(prefs, now)) {
            return;
        }

        // Check message frequency
        if (!shouldSendMessage(prefs, now)) {
            return;
        }

        // Check message limits
        MessageLimits limits = messageLimitsRepository.findByUserId(prefs.getUserId())
            .orElseGet(() -> createDefaultMessageLimits(prefs.getUserId()));

        if (!isWithinLimits(limits)) {
            return;
        }

        // Get user's preferred persona
        Persona persona = personaRepository.findById(prefs.getPreferredPersonaId())
            .orElseGet(() -> personaRepository.findByRequiredPlan(Persona.PlanType.free)
                .stream()
                .findFirst()
                .orElse(null));

        if (persona == null || !persona.getIsActive()) {
            return;
        }

        // Get user's phone number
        User user = userRepository.findById(prefs.getUserId()).orElse(null);
        if (user == null) {
            return;
        }

        // Generate and send message
        sendScheduledMessage(user, persona, limits);
    }

    private boolean isWithinPreferredTimeWindow(UserPreferences prefs, LocalDateTime now) {
        // Default window if not specified (9 AM to 9 PM)
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(21, 0);

        if (prefs.getPreferredTimeWindow() != null) {
            // Parse preferred time window from JSONB
            // Format: {"start": "HH:mm", "end": "HH:mm"}
            // Implementation depends on how you store the time window
        }

        LocalTime currentTime = now.toLocalTime();
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }

    private boolean shouldSendMessage(UserPreferences prefs, LocalDateTime now) {
        // Get last message timestamp for this user
        LocalDateTime lastMessageTime = messageService.getLastMessageTime(prefs.getUserId());
        
        if (lastMessageTime == null) {
            return true;
        }

        long hoursSinceLastMessage = java.time.Duration.between(lastMessageTime, now).toHours();

        MessageFrequency frequency = MessageFrequency.valueOf(prefs.getMessageFrequency());
        return switch (frequency) {
            case DAILY -> hoursSinceLastMessage >= 24;
            case WEEKLY -> hoursSinceLastMessage >= 168;
            case MONTHLY -> hoursSinceLastMessage >= 720;
            default -> false;
        };
    }

    private boolean isWithinLimits(MessageLimits limits) {
        return limits.getDailyCount() < limits.getMaxDailyMessages() &&
               limits.getWeeklyCount() < limits.getMaxWeeklyMessages() &&
               limits.getMonthlyCount() < limits.getMaxMonthlyMessages();
    }

    private MessageLimits createDefaultMessageLimits(Long userId) {
        MessageLimits limits = new MessageLimits();
        limits.setUserId(userId);
        limits.setMaxDailyMessages(30);
        limits.setMaxWeeklyMessages(210); // 30 * 7
        limits.setMaxMonthlyMessages(900); // 30 * 30
        return messageLimitsRepository.save(limits);
    }

    private void sendScheduledMessage(User user, Persona persona, MessageLimits limits) {
        // Select a random sample message or generate one
        String initialMessage = selectInitialMessage(persona);
        
        // Generate AI response using the persona's system prompt
        String aiResponse = messageService.generateAIResponse(initialMessage, persona);
        
        // Send message via Twilio
        twilioService.sendSms(user.getPhoneNumber(), aiResponse);
        
        // Save the message
        Message message = new Message(
            user.getId(),
            initialMessage,
            aiResponse,
            persona.getName(),
            "neutral"
        );
        message.setPersonaId(persona.getId());
        message.setMessageType(MessageContentType.TEXT);
        messageService.saveMessage(message);
        
        // Update limits
        limits.incrementCounts();
        messageLimitsRepository.save(limits);
        
        // Record billable message for pay-as-you-go pricing
        if (user.getPlanType() != PlanType.FREE) {
            billingService.recordMessageSent(user.getId());
        }
    }

    private String selectInitialMessage(Persona persona) {
        List<String> sampleMessages = persona.getSampleMessages();
        if (sampleMessages != null && !sampleMessages.isEmpty()) {
            return sampleMessages.get(random.nextInt(sampleMessages.size()));
        }
        
        // Fallback to a generic message
        return "Hey! How are you doing today?";
    }

    // Add this method to retrieve message limits for external services
    public MessageLimits getMessageLimits(Long userId) {
        return messageLimitsRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultMessageLimits(userId));
    }

    public boolean checkDailyLimit(Long userId) {
        MessageLimits limits = getMessageLimits(userId);
        return limits.getDailyCount() < limits.getMaxDailyMessages();
    }
}
