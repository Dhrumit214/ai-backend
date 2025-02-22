package aifriend.ai_backend.service;

import aifriend.ai_backend.dto.MessageRequest;
import aifriend.ai_backend.dto.MessageResponse;
import aifriend.ai_backend.repository.MessageLimitsRepository;
import aifriend.ai_backend.repository.MessageRepository;
import aifriend.ai_backend.repository.PersonaRepository;
import aifriend.ai_backend.repository.UserPreferencesRepository;
import aifriend.ai_backend.repository.UserRepository;
import aifriend.ai_backend.model.*;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class MessageService {

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private MessageRepository messageRepository;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    
    private final OkHttpClient httpClient = new OkHttpClient();

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public MessageResponse generateResponse(MessageRequest request) {
        String aiResponse = generateAIResponse(request.getMessage(), null);
        
        Message message = new Message(Long.valueOf(request.getUserId()), request.getMessage(), aiResponse, request.getPersona(), "neutral");
        saveMessage(message);

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

        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), jsonRequest);

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

    public LocalDateTime getLastMessageTime(Long userId) {
        Message lastMessage = messageRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
        return lastMessage != null ? lastMessage.getCreatedAt() : null;
    }

    public void deleteMessage(Long messageId) {
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setDeletedAt(LocalDateTime.now());
            messageRepository.save(message);
        });
    }
}
