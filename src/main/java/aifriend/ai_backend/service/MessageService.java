package aifriend.ai_backend.service;

import aifriend.ai_backend.dto.MessageRequest;
import aifriend.ai_backend.dto.MessageResponse;
import aifriend.ai_backend.model.*;
import aifriend.ai_backend.repository.MessageLimitsRepository;
import aifriend.ai_backend.repository.MessageRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageLimitsRepository messageLimitsRepository;
    private final OkHttpClient httpClient;

    @Value("${openai.api.key:test-api-key-for-dev}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    @Autowired
    public MessageService(MessageRepository messageRepository,
                         MessageLimitsRepository messageLimitsRepository,
                         OkHttpClient httpClient) {
        this.messageRepository = messageRepository;
        this.messageLimitsRepository = messageLimitsRepository;
        this.httpClient = httpClient;
    }

    public MessageResponse generateResponse(MessageRequest request) {
        Long userId = Long.valueOf(request.getUserId());
        
        // Check if user has reached daily limit
        MessageLimits limits = getMessageLimits(userId);
        if (limits.getDailyCount() >= limits.getMaxDailyMessages()) {
            return new MessageResponse(
                "Daily message limit of 30 messages reached. Please try again tomorrow or upgrade your plan.",
                request.getPersona(),
                request.getMessage()
            );
        }
        
        String aiResponse = generateAIResponse(request.getMessage(), null);
        
        Message message = new Message(userId, request.getMessage(), aiResponse, request.getPersona(), "neutral");
        saveMessage(message);
        
        // Update the message limits after sending a message
        incrementMessageCount(limits);

        return new MessageResponse(aiResponse, request.getPersona(), request.getMessage());
    }

    public String generateAIResponse(String message, Persona persona) {
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        
        String systemPrompt = persona != null ? 
            persona.getSystemPrompt() : 
            "You are a friendly AI assistant. Be helpful and engaging.";
            
        systemMessage.addProperty("content", systemPrompt);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", message);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(systemMessage);
        messagesArray.add(userMessage);

        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("model", "gpt-4");
        requestBodyJson.add("messages", messagesArray);
        requestBodyJson.addProperty("temperature", 0.7);
        requestBodyJson.addProperty("max_tokens", 150);

        String jsonRequest = requestBodyJson.toString();

        RequestBody requestBody = RequestBody.create(jsonRequest, MediaType.get("application/json"));

        Request request = new Request.Builder()
            .url(OPENAI_URL)
            .post(requestBody)
            .addHeader("Authorization", "Bearer " + openAiApiKey)
            .addHeader("Content-Type", "application/json")
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String jsonResponse = response.body() != null ? response.body().string() : "No response body";

            if (!response.isSuccessful()) {
                System.err.println("OpenAI API Error: " + jsonResponse);
                return "Error: OpenAI API request failed.";
            }

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray choices = jsonObject.getAsJsonArray("choices");
            if (choices != null && choices.size() > 0) {
                JsonObject firstChoice = choices.get(0).getAsJsonObject();
                return firstChoice.getAsJsonObject("message").get("content").getAsString();
            } else {
                return "Error: OpenAI response format unexpected.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: IOException while calling OpenAI.";
        }
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public OffsetDateTime getLastMessageTime(Long userId) {
        Message lastMessage = messageRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
        return lastMessage != null ? lastMessage.getCreatedAt() : null;
    }

    public void deleteMessage(Long messageId) {
        // Since we now use a composite key, we need to find the message first to get its createdAt value
        Optional<Message> messageOpt = messageRepository.findByIdOrderByCreatedAtDesc(messageId);
        messageOpt.ifPresent(message -> {
            message.setDeletedAt(OffsetDateTime.now());
            messageRepository.save(message);
        });
    }
    
    // Helper methods for message limits
    private MessageLimits getMessageLimits(Long userId) {
        return messageLimitsRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultMessageLimits(userId));
    }
    
    private MessageLimits createDefaultMessageLimits(Long userId) {
        MessageLimits limits = new MessageLimits();
        limits.setUserId(userId);
        limits.setMaxDailyMessages(30); // Default 30 messages per day
        limits.setMaxWeeklyMessages(210); // 30 * 7
        limits.setMaxMonthlyMessages(900); // 30 * 30
        return messageLimitsRepository.save(limits);
    }
    
    private void incrementMessageCount(MessageLimits limits) {
        limits.incrementCounts();
        messageLimitsRepository.save(limits);
    }
}
