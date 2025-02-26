package aifriend.ai_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {
    
    @Value("${twilio.account.sid:#{null}}")
    private String accountSid;
    
    @Value("${twilio.auth.token:#{null}}")
    private String authToken;
    
    @Value("${twilio.phone.number:#{null}}")
    private String twilioPhoneNumber;
    
    public void sendSms(String toPhoneNumber, String message) {
        // In a real implementation, we would use the Twilio SDK to send SMS
        // For now, we'll just log the message
        System.out.println("Sending SMS to " + toPhoneNumber + ": " + message);
        
        // Example Twilio implementation (commented out):
        /*
        try {
            Twilio.init(accountSid, authToken);
            Message twilioMessage = Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                message)
                .create();
            System.out.println("Sent message SID: " + twilioMessage.getSid());
        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
        }
        */
    }
}