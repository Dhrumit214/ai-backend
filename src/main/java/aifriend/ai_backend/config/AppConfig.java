package aifriend.ai_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import lombok.Getter;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

@Configuration
@Getter
public class AppConfig {

    // OpenAI API Key (Stored in application.properties)
    @Value("${openai.api.key}")
    private String openaiApiKey;

    // Twilio Credentials (Stored in application.properties)
    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    /**
     * Creates an OkHttpClient bean with configured timeouts
     * @return OkHttpClient
     */
    @Bean
    @Profile("!test") // This bean is not created in the test profile
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    public String getOpenaiApiKey() {
        return openaiApiKey;
    }
    
    public String getTwilioAccountSid() {
        return twilioAccountSid;
    }

    public String getTwilioAuthToken() {
        return twilioAuthToken;
    }

    public String getTwilioPhoneNumber() {
        return twilioPhoneNumber;
    }
}