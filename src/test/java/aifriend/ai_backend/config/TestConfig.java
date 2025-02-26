package aifriend.ai_backend.config;

import aifriend.ai_backend.repository.MessageLimitsRepository;
import aifriend.ai_backend.repository.MessageRepository;
import aifriend.ai_backend.repository.PersonaRepository;
import aifriend.ai_backend.repository.UserPreferencesRepository;
import aifriend.ai_backend.repository.UserRepository;
import aifriend.ai_backend.service.MessageService;
import aifriend.ai_backend.service.TwilioService;
import okhttp3.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public TwilioService twilioService() {
        TwilioService mockTwilioService = Mockito.mock(TwilioService.class);
        // Configure mock behavior if needed
        Mockito.doNothing().when(mockTwilioService).sendSms(Mockito.anyString(), Mockito.anyString());
        return mockTwilioService;
    }
    
    @Bean
    @Primary
    public OkHttpClient okHttpClient() throws IOException {
        // Create a mock OkHttpClient that returns a successful response
        OkHttpClient mockClient = Mockito.mock(OkHttpClient.class);
        Call mockCall = Mockito.mock(Call.class);
        Response mockResponse = new Response.Builder()
            .request(new Request.Builder().url("https://example.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(ResponseBody.create(MediaType.parse("application/json"), 
                  "{\"choices\":[{\"message\":{\"content\":\"Test AI response\"}}]}"))
            .build();
            
        Mockito.when(mockClient.newCall(Mockito.any())).thenReturn(mockCall);
        Mockito.when(mockCall.execute()).thenReturn(mockResponse);
        
        return mockClient;
    }
    
    // Add mock repositories if needed for testing
    
    @Bean
    @Primary
    public MessageRepository messageRepository() {
        return Mockito.mock(MessageRepository.class);
    }
    
    @Bean
    @Primary
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }
    
    @Bean
    @Primary
    public PersonaRepository personaRepository() {
        return Mockito.mock(PersonaRepository.class);
    }
    
    @Bean
    @Primary
    public UserPreferencesRepository userPreferencesRepository() {
        return Mockito.mock(UserPreferencesRepository.class);
    }
    
    @Bean
    @Primary
    public MessageLimitsRepository messageLimitsRepository() {
        return Mockito.mock(MessageLimitsRepository.class);
    }
}