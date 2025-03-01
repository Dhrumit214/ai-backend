package aifriend.ai_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import lombok.Getter;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
    
    /**
     * Configure JSON object mapper for handling JSONB types
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
    
    /**
     * Customize Jackson to handle JSON data types properly
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}