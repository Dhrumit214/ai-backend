package aifriend.ai_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageResponse {
    private String aiResponse; // The AI-generated response
    private String persona; // The AI persona that responded
    private String message;
    private String userId;
    private String response;
    private String sentiment;
    


    // Default constructor
    public MessageResponse() {
    }

    // Constructor with parameters
    public MessageResponse(String message, String persona) {
        this.message = message;
        this.persona = persona;
    }
    public MessageResponse(String aiResponse, String persona, String message) {
        this.aiResponse = aiResponse;
        this.persona = persona;
        this.message = message;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }
}
