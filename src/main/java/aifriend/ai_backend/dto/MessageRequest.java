package aifriend.ai_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private String userId;
    private String persona; // The selected AI persona (e.g., "Supportive Friend")
    private String message; // User's input message
    private String phoneNumber;

    // Constructor with parameters
    public MessageRequest(String message, String persona, String userId) {
        this.message = message;
        this.persona = persona;
        this.userId = userId;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public String getPersona() {
        return persona;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    // If not using Lombok, add these methods:
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

