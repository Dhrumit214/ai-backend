package aifriend.ai_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class AppConfig {

    // OpenAI API Key (Stored in application.properties)
    @Value("${openai.api.key}")
    private String openAiApiKey;

    // Twilio Credentials (Stored in application.properties)
    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    public String getOpenAiApiKey() {
        return openAiApiKey;
    }
    
    public String getTwilioAccountSid() {
        return twilioAccountSid;
    }
}